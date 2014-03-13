
package haui.io;

/** Defines the permissions for a Unix file. */

public class UnixFilePermissions
{
	public static final int OWNER_READABLE = 0400;
	public static final int OWNER_WRITABLE = 0200;
	public static final int OWNER_EXECUTABLE = 0100;
	public static final int GROUP_READABLE = 0040;
	public static final int GROUP_WRITABLE = 0020;
	public static final int GROUP_EXECUTABLE = 0010;
	public static final int OTHER_READABLE = 0004;
	public static final int OTHER_WRITABLE = 0002;
	public static final int OTHER_EXECUTABLE = 0001;

	/** Create a new UnixFilePermissions with no permissions. */

	public UnixFilePermissions ()
	{
		this (0);
	}

	/** Creates a new UnixFilePermissions from the specified bit mask. */

	public UnixFilePermissions (int nPermissions)
	{
		m_nPermissions = nPermissions;
	}

	/** Creates a new UnixFilePermissions object from the specified file permission string.
	 For example, "rwx------". */

	public UnixFilePermissions (String sPermissions)
	{
		setPermissions(sPermissions);
	}

	public boolean equals (Object o)
	{
		if (!(o instanceof UnixFilePermissions))
			return false;

		return ((UnixFilePermissions)o).m_nPermissions == m_nPermissions;
	}

	public int hashCode() {return m_nPermissions;}

	/** Gets the permissions as an integer bit mask. */

	public int getPermissions () {return m_nPermissions;}

	/** Sets the permissions from an integer bit mask. */

	public void setPermissions (int nPermissions)
	{
		if (nPermissions != m_nPermissions)
		{
			m_nPermissions = nPermissions;
			m_sPermissions = null;
		}
	}

	public void setPermissions(String sPermissions)
	{
		if (sPermissions.length () != 9)
			throw new IllegalArgumentException("Invalid permissions: " + sPermissions);

		m_nPermissions = 0;

		setOwnerReadable(sPermissions.charAt (0) == 'r');
		setOwnerWritable(sPermissions.charAt (1) == 'w');
		setOwnerExecutable(sPermissions.charAt (2) == 'x');

		setGroupReadable(sPermissions.charAt (3) == 'r');
		setGroupWritable(sPermissions.charAt (4) == 'w');
		setGroupExecutable(sPermissions.charAt (5) == 'x');

		setOtherReadable(sPermissions.charAt (6) == 'r');
		setOtherWritable(sPermissions.charAt (7) == 'w');
		setOtherExecutable(sPermissions.charAt (8) == 'x');

		m_sPermissions = sPermissions;
	}

	public boolean isOwnerReadable () {return (m_nPermissions & OWNER_READABLE) != 0;}
	public boolean isOwnerWritable () {return (m_nPermissions & OWNER_WRITABLE) != 0;}
	public boolean isOwnerExecutable () {return (m_nPermissions & OWNER_EXECUTABLE) != 0;}
	public boolean isGroupReadable () {return (m_nPermissions & GROUP_READABLE) != 0;}
	public boolean isGroupWritable () {return (m_nPermissions & GROUP_WRITABLE) != 0;}
	public boolean isGroupExecutable () {return (m_nPermissions & GROUP_EXECUTABLE) != 0;}
	public boolean isOtherReadable () {return (m_nPermissions & OTHER_READABLE) != 0;}
	public boolean isOtherWritable () {return (m_nPermissions & OTHER_WRITABLE) != 0;}
	public boolean isOtherExecutable () {return (m_nPermissions & OTHER_EXECUTABLE) != 0;}

	public void setOwnerReadable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OWNER_READABLE);

		else setPermissions(m_nPermissions & (~OWNER_READABLE));
	}

	public void setOwnerWritable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OWNER_WRITABLE);

		else setPermissions(m_nPermissions & (~OWNER_WRITABLE));
	}

	public void setOwnerExecutable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OWNER_EXECUTABLE);

		else setPermissions(m_nPermissions & (~OWNER_EXECUTABLE));
	}

	public void setGroupReadable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | GROUP_READABLE);

		else setPermissions(m_nPermissions & (~GROUP_READABLE));
	}

	public void setGroupWritable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | GROUP_WRITABLE);

		else setPermissions(m_nPermissions & (~GROUP_WRITABLE));
	}

	public void setGroupExecutable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | GROUP_EXECUTABLE);

		else setPermissions(m_nPermissions & (~GROUP_EXECUTABLE));
	}

	public void setOtherReadable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OTHER_READABLE);

		else setPermissions(m_nPermissions & (~OTHER_READABLE));
	}

	public void setOtherWritable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OTHER_WRITABLE);

		else setPermissions(m_nPermissions & (~OTHER_WRITABLE));
	}

	public void setOtherExecutable (boolean b)
	{
		if (b)
			setPermissions(m_nPermissions | OTHER_EXECUTABLE);

		else setPermissions(m_nPermissions & (~OTHER_EXECUTABLE));
	}

	public String toString ()
	{
		if (m_sPermissions == null)
		{
			StringBuffer buffer = new StringBuffer ("---------");

			if ((m_nPermissions & OWNER_READABLE) != 0)
				buffer.setCharAt (0, 'r');

			if ((m_nPermissions & OWNER_WRITABLE) != 0)
				buffer.setCharAt (1, 'w');

			if ((m_nPermissions & OWNER_EXECUTABLE) != 0)
				buffer.setCharAt (2, 'x');

			if ((m_nPermissions & GROUP_READABLE) != 0)
				buffer.setCharAt (3, 'r');

			if ((m_nPermissions & GROUP_WRITABLE) != 0)
				buffer.setCharAt (4, 'w');

			if ((m_nPermissions & GROUP_EXECUTABLE) != 0)
				buffer.setCharAt (5, 'x');

			if ((m_nPermissions & OTHER_READABLE) != 0)
				buffer.setCharAt (6, 'r');

			if ((m_nPermissions & OTHER_WRITABLE) != 0)
				buffer.setCharAt (7, 'w');

			if ((m_nPermissions & OTHER_EXECUTABLE) != 0)
				buffer.setCharAt (8, 'x');

			m_sPermissions = buffer.toString();
		}

		return m_sPermissions;
	}

	private int m_nPermissions;
	private String m_sPermissions;
}
