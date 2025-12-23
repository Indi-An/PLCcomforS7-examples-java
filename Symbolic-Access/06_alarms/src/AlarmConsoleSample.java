
import com.indian.plccom.fors7.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Java 11 console sample that demonstrates PLCcom alarm integration: 1) Browse
 * current active alarms (snapshot) 2) Subscribe to live alarm notifications
 * (events) 3) Print details and acknowledge alarms from the console
 *
 * The goal is to show a "minimal but complete" integration pattern for
 * customers.
 */
public final class AlarmConsoleSample {

	// Single lock so prompt + event output does not become unreadable.
	private static final Object CONSOLE_LOCK = new Object();

	public static void main(String[] args) throws Exception {
		// Allow overriding PLC IP from CLI: mvn -q exec:java -Dexec.args="192.168.1.10"
		final String ip = (args.length > 0) ? args[0] : "192.168.1.10";

		// Use the OS default locale. This matches the Swing examples
		// (Locale.getDefault()).
		final Locale appLocale = Locale.getDefault();

		// License/authentication: user must provide correct values.
		authentication.setUser("");
		authentication.setSerial("");

		// ---------------------------------------------------------------------
		// 1) CONNECT (replace/adapt this block to your fork’s connection pattern)
		// ---------------------------------------------------------------------
		// In the C# example you used Tls13Device. The Java fork typically offers a
		// similar device.
		// If your fork uses a different device type or different auth setup, adjust
		// here.
		final SymbolicDevice device = new Tls13Device(ip);

		// The concrete ConnectResult type depends on your fork/API version.
		// We therefore treat it as Object and only check "quality" via reflection-like
		// pattern:
		// - if it offers isQualityGood()/isQualityBad() we use it
		// - otherwise we simply proceed and let subsequent calls fail clearly
		final Object connectResult = tryConnect(device);

		if (!isQualityGood(connectResult)) {
			safePrintln("Connection failed: " + String.valueOf(connectResult));
			tryDisconnect(device);
			return;
		}

		safePrintln("Connected successfully.");
		safePrintln("Type 'help' for commands.\n");

		// Wrap alarm logic in a helper so main() stays readable.
		final PlcAlarmConsole alarms = new PlcAlarmConsole(device, appLocale);

		// ---------------------------------------------------------------------
		// 2) INITIAL SNAPSHOT: read current state first
		// ---------------------------------------------------------------------
		alarms.loadActiveAlarmsSnapshot();

		// ---------------------------------------------------------------------
		// 3) LIVE SUBSCRIPTION: then subscribe to future notifications
		// ---------------------------------------------------------------------
		alarms.subscribe();

		// Ensure clean shutdown if the JVM is terminated (Ctrl+C / SIGTERM).
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				alarms.close();
			} catch (Exception ignored) {
			}
			tryDisconnect(device);
		}));

		// Simple interactive command loop.
		runCommandLoop(alarms);

		// Normal exit path.
		alarms.close();
		tryDisconnect(device);
	}

	private static void runCommandLoop(PlcAlarmConsole alarms) throws Exception {
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		printHelp();

		while (true) {
			// Print prompt; lock ensures event output does not cut into it.
			synchronized (CONSOLE_LOCK) {
				System.out.print("> ");
			}

			final String line = in.readLine();
			if (line == null) {
				// EOF (e.g., piped input ended)
				return;
			}

			final String trimmed = line.trim();
			if (trimmed.isEmpty()) {
				continue;
			}

			final String[] parts = trimmed.split("\\s+", 2);
			final String cmd = parts[0].toLowerCase(Locale.ROOT);
			final String arg = (parts.length > 1) ? parts[1].trim() : "";

			switch (cmd) {
			case "h":
			case "help":
				printHelp();
				break;

			case "l":
			case "list":
				alarms.printActiveAlarms();
				break;

			case "d":
			case "details":
				if (arg.isEmpty()) {
					safePrintln("Usage: details <alarmId>");
				} else {
					alarms.printAlarmDetails(arg);
				}
				break;

			case "a":
			case "ack":
				if (arg.isEmpty()) {
					safePrintln("Usage: ack <alarmId>");
				} else {
					alarms.acknowledge(arg);
				}
				break;

			case "ackall":
				alarms.acknowledgeAll();
				break;

			case "q":
			case "quit":
			case "exit":
				return;

			default:
				safePrintln("Unknown command '" + cmd + "'. Type 'help'.");
				break;
			}
		}
	}

	private static void printHelp() {
		safePrintln("");
		safePrintln("Commands:");
		safePrintln("  help                Show this help");
		safePrintln("  list                List current active alarms (cached snapshot)");
		safePrintln("  details <alarmId>   Print full details for one alarm");
		safePrintln("  ack <alarmId>       Acknowledge one alarm (if acknowledgeable)");
		safePrintln("  ackall              Acknowledge all acknowledgeable active alarms");
		safePrintln("  quit                Exit");
		safePrintln("");
	}

	private static void safePrintln(String msg) {
		synchronized (CONSOLE_LOCK) {
			System.out.println(msg);
		}
	}

	// --- Connection helpers -------------------------------------------------

	private static Object tryConnect(SymbolicDevice device) {
		// Keep the example resilient across minor API differences:
		// - If the fork returns a ConnectResult (preferred), we just propagate it.
		// - If connect() is void in a fork variant, we return a dummy object.
		try {
			return device.getClass().getMethod("connect").invoke(device);
		} catch (NoSuchMethodException e) {
			// If your fork uses a different method name, adjust here.
			throw new RuntimeException("Device has no connect() method. Please adapt tryConnect().", e);
		} catch (Exception e) {
			return e;
		}
	}

	private static void tryDisconnect(SymbolicDevice device) {
		// Try common disconnect spellings used across forks.
		tryInvokeNoArgs(device, "disConnect");
		tryInvokeNoArgs(device, "disconnect");
		tryInvokeNoArgs(device, "close");
	}

	private static void tryInvokeNoArgs(Object target, String methodName) {
		try {
			target.getClass().getMethod(methodName).invoke(target);
		} catch (Exception ignored) {
			// Intentionally ignored: we probe multiple method names.
		}
	}

	private static boolean isQualityGood(Object result) {
		if (result == null)
			return true;

		// If an exception object was returned, treat it as failure.
		if (result instanceof Exception)
			return false;

		// Try isQualityGood() / isQualityBad() by reflection to avoid hard coupling.
		try {
			Object ok = result.getClass().getMethod("isQualityGood").invoke(result);
			if (ok instanceof Boolean)
				return (Boolean) ok;
		} catch (Exception ignored) {
		}

		try {
			Object bad = result.getClass().getMethod("isQualityBad").invoke(result);
			if (bad instanceof Boolean)
				return !((Boolean) bad);
		} catch (Exception ignored) {
		}

		// If we cannot evaluate quality, assume "good" and let subsequent calls show
		// errors explicitly.
		return true;
	}

	/**
	 * Console alarm client that mirrors the Swing implementation: -
	 * browseActiveAlarms() for the initial snapshot - addAlarmListener() for
	 * event-based updates - remove alarm on NotActive/Canceled; else update or add
	 * - acknowledge via ackAlarm(alarmId)
	 */
	private static final class PlcAlarmConsole implements AlarmNotificationListener, AutoCloseable {

		private final SymbolicDevice device;
		private final Locale appLocale;

		// Active alarms keyed by AlarmId string for user-friendly console commands.
		private final ConcurrentHashMap<String, AlarmNotification> active = new ConcurrentHashMap<>();

		// Optional: store last N information notifications (not acknowledgeable).
		private final ConcurrentLinkedQueue<AlarmNotification> infos = new ConcurrentLinkedQueue<>();
		private static final int MAX_INFOS = 50;

		// Formatter used for stable timestamp output in the console.
		private final DateTimeFormatter tsFmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());

		private volatile boolean subscribed;

		PlcAlarmConsole(SymbolicDevice device, Locale appLocale) {
			this.device = Objects.requireNonNull(device, "device");
			this.appLocale = Objects.requireNonNull(appLocale, "appLocale");
		}

		void loadActiveAlarmsSnapshot() {
			// This matches your Swing LoadAlarms():
			// - BrowseAlarmsRequest(Locale.getDefault())
			// - browseActiveAlarms(request)
			// - sort by AlarmPriority
			// - choose best locale for AlarmText and show it
			final BrowseAlarmsRequest req = new BrowseAlarmsRequest(appLocale);
			final BrowseAlarmsResult res = device.browseActiveAlarms(req);

			if (res == null || res.isQualityBad()) {
				safePrintln("ERROR: browseActiveAlarms failed. " + (res != null ? res.getMessage() : "result=null"));
				return;
			}

			final List<AlarmNotification> sorted = res.getAlarms().stream()
					.sorted(Comparator.comparing(AlarmNotification::getAlarmPriority)).collect(Collectors.toList());

			// Populate local cache (snapshot).
			for (AlarmNotification a : sorted) {
				active.put(a.getAlarmId().toString(), a);
			}

			safePrintln("Loaded " + active.size() + " active alarms (initial snapshot).");
			printActiveAlarms();
		}

		void subscribe() {
			if (subscribed)
				return;

			// Equivalent to your Swing SubscribeAlarms(): mDevice.addAlarmListener(this)
			device.addAlarmListener(this);
			subscribed = true;

			safePrintln("Subscribed to AlarmNotification (live updates enabled).");
		}

		void unsubscribe() {
			if (!subscribed)
				return;

			// Your Swing code uses removeAlarmNotificationHandler(this).
			// Some forks may expose a different remove method name; we probe common
			// variants.
			boolean removed = tryRemoveListener(device, this);
			subscribed = false;

			safePrintln(removed ? "Unsubscribed from AlarmNotification."
					: "Unsubscribe: no known remove-method found (listener may remain registered).");
		}

		static boolean tryRemoveListener(SymbolicDevice device, AlarmNotificationListener listener) {
			// We try multiple method names to stay compatible with variations:
			// - removeAlarmNotificationHandler(listener) (as seen in your Swing example)
			// - removeAlarmListener(listener) (common symmetrical naming)
			for (String name : new String[] { "removeAlarmNotificationHandler", "removeAlarmListener" }) {
				try {
					device.getClass().getMethod(name, AlarmNotificationListener.class).invoke(device, listener);
					return true;
				} catch (Exception ignored) {
				}
			}
			return false;
		}

		void printActiveAlarms() {
			// Print a stable view. ConcurrentHashMap iteration is weakly consistent, good
			// enough for a demo.
			safePrintln("");
			safePrintln("Active alarms: " + active.size());
			safePrintln(
					"----------------------------------------------------------------------------------------------------");
			safePrintln("AlarmId | Priority | MessageType | State | AlarmNo | TS Coming (local)        | AlarmText");
			safePrintln(
					"----------------------------------------------------------------------------------------------------");

			active.values().stream().sorted(Comparator.comparing(AlarmNotification::getAlarmPriority)
					.thenComparing(a -> a.getAlarmId().toString())).forEach(a -> {
						String id = a.getAlarmId().toString();
						String pri = String.valueOf(a.getAlarmPriority());
						String type = String.valueOf(a.getMessageType());
						String state = String.valueOf(a.getAlarmState());
						String no = String.valueOf(a.getAlarmNumber());
						String ts = formatInstant(a.getTimeStampComing());
						String txt = getText(a, eAlarmTextType.AlarmText, chooseBestTextLocale(a));

						safePrintln(id + " | " + pri + " | " + type + " | " + state + " | " + no + " | "
								+ padRight(ts, 22) + " | " + txt);
					});

			safePrintln(
					"----------------------------------------------------------------------------------------------------");
			safePrintln("");
		}

		void printAlarmDetails(String alarmId) {
			final AlarmNotification alarm = active.get(alarmId);
			if (alarm == null) {
				safePrintln("Alarm '" + alarmId + "' not found in active cache.");
				return;
			}

			// Mirrors AlarmDetails.setFields():
			// - choose locale (default locale if present, else first PLC-provided locale)
			// - group/filter AlarmTextEntries by culture
			// - print all text types
			final Locale textLocale = chooseBestTextLocale(alarm);

			safePrintln("");
			safePrintln("Alarm Details - AlarmId=" + alarm.getAlarmId());
			safePrintln(
					"----------------------------------------------------------------------------------------------------");
			safePrintln("Producer:            " + alarm.getAlarmProducer());
			safePrintln("MessageType:         " + alarm.getMessageType());
			safePrintln("State:               " + alarm.getAlarmState());
			safePrintln("AlarmClass:          " + alarm.getAlarmClass());
			safePrintln("AlarmNumber:         " + alarm.getAlarmNumber());
			safePrintln("Priority:            " + alarm.getAlarmPriority());
			safePrintln("");
			safePrintln("Selected Text Locale: " + textLocale + " (languageTag=" + textLocale.toLanguageTag() + ")");
			safePrintln("");
			safePrintln("TS Coming:           " + formatInstant(alarm.getTimeStampComing()));
			safePrintln("TS Going:            " + formatInstant(alarm.getTimeStampGoing()));
			safePrintln("TS Ack:              " + formatInstant(alarm.getTimeStampAck()));
			safePrintln("");

			safePrintln("Texts:");
			safePrintln("InfoText:            " + getText(alarm, eAlarmTextType.InfoText, textLocale));
			safePrintln("AlarmText:           " + getText(alarm, eAlarmTextType.AlarmText, textLocale));
			safePrintln("AdditionalText1:     " + getText(alarm, eAlarmTextType.AdditionalText1, textLocale));
			safePrintln("AdditionalText2:     " + getText(alarm, eAlarmTextType.AdditionalText2, textLocale));
			safePrintln("AdditionalText3:     " + getText(alarm, eAlarmTextType.AdditionalText3, textLocale));
			safePrintln("AdditionalText4:     " + getText(alarm, eAlarmTextType.AdditionalText4, textLocale));
			safePrintln("AdditionalText5:     " + getText(alarm, eAlarmTextType.AdditionalText5, textLocale));
			safePrintln("AdditionalText6:     " + getText(alarm, eAlarmTextType.AdditionalText6, textLocale));
			safePrintln("AdditionalText7:     " + getText(alarm, eAlarmTextType.AdditionalText7, textLocale));
			safePrintln("AdditionalText8:     " + getText(alarm, eAlarmTextType.AdditionalText8, textLocale));
			safePrintln("AdditionalText9:     " + getText(alarm, eAlarmTextType.AdditionalText9, textLocale));

			safePrintln(
					"----------------------------------------------------------------------------------------------------");
			safePrintln("");
		}

		void acknowledge(String alarmId) {
			final AlarmNotification alarm = active.get(alarmId);
			if (alarm == null) {
				safePrintln("Alarm '" + alarmId + "' not found in active cache.");
				return;
			}

			// Same rule as the Swing UI: acknowledge only acknowledgeable alarms.
			if (alarm.getMessageType() != eAlarmMessageType.AcknowledgeableAlarm) {
				safePrintln(
						"Alarm '" + alarmId + "' is not acknowledgeable (MessageType=" + alarm.getMessageType() + ").");
				return;
			}

			final OperationResult res = device.ackAlarm(alarm.getAlarmId());
			if (res != null && res.isQualityGood()) {
				safePrintln("ACK OK: AlarmId=" + alarm.getAlarmId() + " Quality=" + res.getQuality());
			} else {
				safePrintln("ACK FAILED: AlarmId=" + alarm.getAlarmId() + " Quality="
						+ (res != null ? res.getQuality() : "null") + " Message="
						+ (res != null ? res.getMessage() : "null"));
			}
		}

		void acknowledgeAll() {
			// Convenience for demos: iterate the cache and ACK all acknowledgeable alarms.
			final List<String> ids = active.values().stream()
					.filter(a -> a.getMessageType() == eAlarmMessageType.AcknowledgeableAlarm)
					.map(a -> a.getAlarmId().toString()).distinct().collect(Collectors.toList());

			if (ids.isEmpty()) {
				safePrintln("No acknowledgeable alarms found.");
				return;
			}

			safePrintln("Acknowledging " + ids.size() + " alarm(s) ...");
			for (String id : ids) {
				acknowledge(id);
			}
		}

		@Override
		public void onAlarm(Object sender, AlarmNotification alarmNotification) {
			// This is the event callback used in your Swing example.
			// It may be invoked from a background thread, so we:
			// - update ConcurrentHashMap (thread-safe)
			// - print using console lock (thread-safe and readable)
			if (alarmNotification == null)
				return;

			final String id = alarmNotification.getAlarmId().toString();

			switch (alarmNotification.getMessageType()) {

			case AcknowledgeableAlarm:
			case NonAcknowledgeableAlarm: {
				// If the alarm becomes inactive (NotActive) or is canceled, remove it from
				// active cache.
				// Otherwise update/add the newest snapshot.
				final boolean remove = alarmNotification.getAlarmState() == eAlarmState.NotActive
						|| alarmNotification.getAlarmState() == eAlarmState.Canceled;

				if (remove) {
					active.remove(id);
					safePrintln("[ALARM REMOVED] id=" + id + " state=" + alarmNotification.getAlarmState() + " text="
							+ getText(alarmNotification, eAlarmTextType.AlarmText,
									chooseBestTextLocale(alarmNotification)));
				} else {
					active.put(id, alarmNotification);
					safePrintln("[ALARM UPDATE ] id=" + id + " type=" + alarmNotification.getMessageType() + " state="
							+ alarmNotification.getAlarmState() + " text=" + getText(alarmNotification,
									eAlarmTextType.AlarmText, chooseBestTextLocale(alarmNotification)));
				}
				break;
			}

			case InformationNotification: {
				// Information messages are not part of the active alarm list.
				// We still print them and keep a bounded history for debugging.
				enqueueInfo(alarmNotification);
				safePrintln("[INFO        ] id=" + id + " ts=" + formatInstant(alarmNotification.getTimeStampComing())
						+ " text=" + getText(alarmNotification, eAlarmTextType.AlarmText,
								chooseBestTextLocale(alarmNotification)));
				break;
			}

			default:
				// Defensive default: future message types should not be silently ignored.
				safePrintln("[UNKNOWN     ] id=" + id + " messageType=" + alarmNotification.getMessageType());
				break;
			}
		}

		private void enqueueInfo(AlarmNotification info) {
			infos.add(info);
			while (infos.size() > MAX_INFOS) {
				infos.poll();
			}
		}

		private String formatInstant(Instant ts) {
			if (ts == null)
				return "";
			// Same approach as the Swing load: Instant -> local date-time output.
			return tsFmt.format(ts.atZone(ZoneId.systemDefault()));
		}

		private Locale chooseBestTextLocale(AlarmNotification alarm) {
			// Mirrors your locale decision in AlarmFunctions + AlarmDetails:
			// - if app locale exists in TextCultureInfos -> use it
			// - else fallback to the first PLC-provided locale
			final Map<Integer, Locale> cultures = alarm.getTextCultureInfos();
			if (cultures == null || cultures.isEmpty())
				return appLocale;

			// The Swing code searches by value equality to see if Locale.getDefault() is
			// available.
			boolean hasAppLocale = cultures.values().stream().anyMatch(appLocale::equals);
			return hasAppLocale ? appLocale : cultures.values().iterator().next();
		}

		private String getText(AlarmNotification alarm, eAlarmTextType type, Locale locale) {
			// Mirrors AlarmDetails.setFields():
			// - group by culture
			// - select list for the chosen locale
			// - pick text by AlarmTextType
			final List<PlcAlarmTextEntry> entries = OptionalTexts.byLocale(alarm, locale);

			final Function<eAlarmTextType, String> pick = t -> entries.stream().filter(e -> e.getAlarmTextType() == t)
					.map(PlcAlarmTextEntry::getText).findFirst().orElse("");

			return pick.apply(type);
		}

		@Override
		public void close() {
			unsubscribe();
		}

		// Small helper that isolates the "entries by locale" logic, so getText() stays
		// readable.
		private static final class OptionalTexts {
			static List<PlcAlarmTextEntry> byLocale(AlarmNotification alarm, Locale locale) {
				try {
					if (alarm.getAlarmTextEntries() == null)
						return Collections.emptyList();

					Map<Locale, List<PlcAlarmTextEntry>> grouped = alarm.getAlarmTextEntries().stream()
							.collect(Collectors.groupingBy(PlcAlarmTextEntry::getCulture));

					return grouped.getOrDefault(locale, Collections.emptyList());
				} catch (Exception ex) {
					// Never throw inside event-heavy code paths; return empty text instead.
					return Collections.emptyList();
				}
			}
		}

		private static String padRight(String s, int width) {
			if (s == null)
				s = "";
			if (s.length() >= width)
				return s;
			StringBuilder sb = new StringBuilder(width);
			sb.append(s);
			while (sb.length() < width)
				sb.append(' ');
			return sb.toString();
		}
	}
}
