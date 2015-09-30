package id.ac.itb.digim.analytics.distance;

import java.util.List;

/**
 * Created by TOLEP on 29/09/2015.
 */
public class DiffChainCode {

    public static int editDistanceInt(List<Integer> word1, List<Integer> word2) {
        int len1 = word1.size();
        int len2 = word2.size();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            int c1 = word1.get(i);
            for (int j = 0; j < len2; j++) {
                int c2 = word2.get(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }


    public static int editDistanceDouble(List<Integer> word1, List<Double> word2) {
        int len1 = word1.size();
        int len2 = word2.size();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            int c1 = word1.get(i);
            for (int j = 0; j < len2; j++) {
                int c2 = word2.get(j).intValue();

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }


    public static int calcDiffInt(List<Integer> list1, List<Integer> list2) {
        int[] listCount1 = new int[8];
        for (int i = 0; i < list1.size(); i++) {
            int code = list1.get(i);
            listCount1[code]++;
        }

        int[] listCount2 = new int[8];
        for (int i = 0; i < list2.size(); i++) {
            int code = list2.get(i);
            listCount2[code]++;
        }

        int result = 0;
        for (int i = 0; i < 8; i++) {
            //result += Math.abs(Math.log(listCount1[i]+1) - Math.log(listCount2[i]+1));
            result += Math.abs(listCount1[i] - listCount2[i]);
        }
        return result;
    }

    public static int calcDiffDouble(List<Integer> list1, List<Double> list2) {
        int[] listCount1 = new int[8];
        for (int i = 0; i < list1.size(); i++) {
            int code = list1.get(i).intValue();
            listCount1[code]++;
        }

        int[] listCount2 = new int[8];
        for (int i = 0; i < list2.size(); i++) {
            int code = list2.get(i).intValue();
            listCount2[code]++;
        }

        int result = 0;
        for (int i = 0; i < 8; i++) {
            //result += Math.abs(Math.log(listCount1[i]+1) - Math.log(listCount2[i]+1));
            result += Math.abs(listCount1[i] - listCount2[i]);
        }
        return result;
    }

}
