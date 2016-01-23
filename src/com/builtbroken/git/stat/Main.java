package com.builtbroken.git.stat;

import com.builtbroken.git.stat.obj.Commit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple program that process a .git project for statistic data.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/23/2016.
 */
public class Main
{
    public static void main(String... args) throws Exception
    {
        //TODO change
        File file = new File("F:/github/1.7/VoltzEngine");

        //Build command
        ProcessBuilder builder = new ProcessBuilder("git", "log", "--pretty=format:\"c:%H,%an<%ae>,%ad\"", "--shortstat");
        //Set run directory
        builder.directory(file);
        //Fire command
        Process p = builder.start();
        parseLog(p.getInputStream());
        //printLog(p.getInputStream());
    }

    private static void printLog(InputStream stream) throws IOException
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        String line = r.readLine();
        while (line != null)
        {
            System.out.println(line);
            line = r.readLine();
        }
    }

    private static void parseLog(InputStream stream) throws IOException
    {
        //Get command output for parsing
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        List<Commit> commits = new ArrayList();

        //Parse data
        String line = r.readLine();
        Commit commit = null;
        while (line != null)
        {
            if (line.startsWith("c:"))
            {
                commit = newCommit(line, r);
                if (commit != null)
                {
                    commits.add(commit);
                }
            }
            else if (line.contains("insertion") || line.contains("deletion") || line.contains("insertions"))
            {
                commit.changesShort = line.trim();
            }
            else if (!line.isEmpty())
            {
                System.out.println("UnknownLine: " + line);
            }
            line = r.readLine();
        }
    }


    /**
     * Generates a commit from a reader, assume that the order is always the
     * same.
     *
     * @param line
     * @param r
     * @return
     */
    private static Commit newCommit(final String line, final BufferedReader r)
    {
        try
        {
            Commit commit = new Commit();
            String[] split = line.replace("c:", "").split(",");
            commit.id = split[0];
            commit.author = split[1];
            //Parse date
            String date = split[2];
            String[] splitA = date.split("\\s+");
            //Sanity check
            if (splitA.length != 6)
            {
                System.out.println("Failed to parse date when creating " + line);
                for (String s : splitA)
                {
                    System.out.println(s);
                }
                return null;
            }
            String[] time = splitA[3].split(":");
            //Sanity check
            if (time.length != 3)
            {
                System.out.println("Failed to parse time when creating " + line);
                return null;
            }

            commit.dayOfWeek = splitA[0];
            commit.month = splitA[1];
            commit.day = splitA[2];

            commit.hour = time[0];
            commit.min = time[1];
            commit.second = time[2];

            commit.year = splitA[4];
            commit.timeZone = splitA[5];

            return commit;
        }
        catch (Exception e)
        {
            System.out.println("Failed to parse " + line);
            e.printStackTrace();
        }
        return null;
    }
}
