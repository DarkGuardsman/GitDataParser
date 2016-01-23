package com.builtbroken.git.stat.obj;

/**
 * A git commit data object
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/23/2016.
 */
public class Commit
{
    public String author;
    public String message;
    public String id;

    public String dayOfWeek;
    public String month;
    public String day;
    public String hour;
    public String min;
    public String second;
    public String year;
    public String timeZone;

    public String changes;
    public String changesShort;

}
