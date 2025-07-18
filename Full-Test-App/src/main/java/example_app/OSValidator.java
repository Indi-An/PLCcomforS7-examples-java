package example_app;

class OSValidator {
	private static String OS = System.getProperty("os.name").toLowerCase();

	enum eOS {
		not_Supported, Windows, MacOS, Unix_LINUX, Solaris
	}

	static eOS getOS() {
		if (OS.indexOf("win") >= 0)
			return eOS.Windows;
		else if (OS.indexOf("mac") >= 0)
			return eOS.MacOS;
		else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)
			return eOS.Unix_LINUX;
		else if (OS.indexOf("sunos") >= 0)
			return eOS.Solaris;
		else
			return eOS.not_Supported;
	}

	static String getFolderSeparator() {
		switch ((eOS) getOS()) {
		case Windows:
			return "\\";
		case Solaris:
		case Unix_LINUX:
		case MacOS:
		case not_Supported:
		default:
			return "/";
		}
	}
}
