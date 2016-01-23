package com.builtbroken.git.stat;

import com.builtbroken.git.stat.obj.Commit;
import com.builtbroken.jlib.lang.StringHelpers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        print("Starting.....");
        print("Parsing .git folder");
        //TODO change
        File file = new File("F:/github/1.7/VoltzEngine");
        print("\tPath: " + file.getAbsolutePath());
        //Build command
        ProcessBuilder builder = new ProcessBuilder("git", "log", "--pretty=format:\"c:%H,%an<%ae>,%ad\"", "--shortstat");
        //Set run directory
        builder.directory(file);

        print("Firing commands....");
        //Start timer
        long time = System.nanoTime();

        //Fire command
        Process p = builder.start();
        //End timer, convert to time taken
        time = System.nanoTime() - time;
        print("\tFinished in " + StringHelpers.formatNanoTime(time));

        print("Parsing output....");
        //Start timer
        time = System.nanoTime();
        List<Commit> commits = parseLog(p.getInputStream());
        //End timer, convert to time taken
        time = System.nanoTime() - time;

        print("\tFinished in " + StringHelpers.formatNanoTime(time));
        print("\t" + commits.size() + " commits");

        //Take a random sample to represent the commits
        print("Taking Random Samples....");

        //Get random commits, ensure non-repeating, and good data
        Random random = new Random();
        List<Integer> ints = new ArrayList();
        List<Commit> sampleCommits = new ArrayList<Commit>();
        for (int i = 0; i < 35; )
        {
            int rng = random.nextInt(3500);

            if (!ints.contains(rng))
            {
                ints.add(rng);
                Commit commit = commits.get(rng);
                if (commit.changesShort != null && !commit.changesShort.isEmpty())
                {
                    System.out.println("Commit: " + rng);
                    System.out.println("\t" + commit);
                    System.out.println("\t" + commit.changesShort);
                    sampleCommits.add(commit);
                    i++;
                }
            }
        }
    }

    static void print(String msg)
    {
        System.out.println(msg);
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

    private static List<Commit> parseLog(InputStream stream) throws IOException
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
        return commits;
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
            commit.commitHash = split[0];
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
