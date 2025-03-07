package opt.easyjmetal.problem.onlinemix;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestFun {
    public static void sort(double[][] ob, int[] order) {
        Arrays.sort(ob, (o1, o2) -> {    //降序排列
            double[] one = (double[]) o1;
            double[] two = (double[]) o2;
            for (int i = 0; i < order.length; i++) {
                int k = order[i];
                if (one[k] > two[k]) return 1; //返回值大于0，将前一个目标值和后一个目标值交换
                else if (one[k] < two[k]) return -1;
                else continue;
            }
            return 0;
        });
    }

    /**
     * 返回一个0-len之间的整数
     * @param a
     * @param len
     * @return
     */
    public static int getInt(double a, int len) {
        double[] sequence = getHDsequence(0.29583, 1000);
        int index = (int) (Math.ceil(a * 1000));
        if (index == 1000) {
            index = 0;
        }
        if (a != 0) {
            a = sequence[index];  //ceil向无穷大方向取整,将随机产生的数转换为混沌序列
        }
        if (a == 1) {
            return len;
        } else {
            return (int) (Math.floor((len + 1) * a)); //取整（舍掉小数）
        }
    }

    public static double[] getHDsequence(double x0, int num) {  //获取混沌序列
        double[] r = new double[num];
        double xn = x0;
        for (int i = 0; i < num; i++) {
            xn = 4 * xn * (1 - xn);
            r[i] = xn;
        }
        return r;
    }

    public static double getMax(double[] array) { //取得一维数组中的最大值
        double max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * 计算供油罐个数
     *
     * @param a 调度计划
     * @return
     */
    public static double gNum(List<List<Double>> a) {
        // ODF格式：蒸馏塔号 | 油罐号1 | 开始供油时间 | 结束供油时间 | 原油类型 | 油罐号2
        // ODT格式：蒸馏塔号 | 油罐号  | 开始供油时间 | 结束供油时间 | 原油类型 | 转运速度
        List<Double> TKset = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            // 根据ODF进行计算
            if (a.get(i).get(0) <= 3) {
                TKset.add(a.get(i).get(1));
                // 判断是否还需要再使用一个供油罐
                if (a.get(i).size() > 5) {
                    TKset.add(a.get(i).get(5));
                }
            }
        }
        HashSet h = new HashSet(TKset);
        return h.size();
    }

    public static double gChange(Object[][] TKS, List<List<Double>> a) { //油罐切换次数
        // 初始装有的原油类型    TKS格式：容量  原油类型  已有容量 蒸馏塔  供油开始时间 供油结束时间  供油罐编号  混合原油类型集合
        Map<String, Integer> res = new HashMap<>();
        for (int i = 0; i < TKS.length; i++) {
            if (Integer.parseInt(TKS[i][2].toString()) > 0) {
                String key = "TK" + (i + 1);
                if (!res.containsKey(key)) {
                    res.put(key, 0);
                }
                res.put(key, res.get(key) + 1);
            }
        }

        // 后期转运的原油类型      ODT格式：蒸馏塔号 | 油罐号 | 开始供油时间 | 结束供油时间 | 原油类型
        List<List<Double>> lists = a.stream()
                .filter(e -> e.get(0) == 4 && e.get(4) != 0)        // 过滤出转运记录，排除停运
                .sorted((e1, e2) -> (int) (e1.get(2) - e2.get(2)))  // 按照转运开始时间排序
                .collect(Collectors.toList());
        for (int i = 0; i < lists.size(); i++) {
            int tk = (int) lists.get(i).get(1).doubleValue();
            String key = "TK" + tk;
            if (!res.containsKey(key)) {
                res.put(key, 0);
            }
            res.put(key, res.get(key) + 1);
        }

        // 统计各个油罐的使用次数
        int count = 0;
        for (String key : res.keySet()) {
            count += res.get(key);
        }

        return count;
    }

    /**
     * 计算管道混合成本
     *
     * @param a
     * @param c1
     * @return
     */
    public static double gDmix(List<List<Double>> a, int[][] c1) {
        double sum = 0;
        int K = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).get(0) == 4d) {
                K = i;
                break;
            }
        }
        double[][] m1 = new double[6][6];//用于存放混合次数
        // 过滤掉管道停运的操作
        List<List<Double>> PIPE = new ArrayList<>();
        for (int i = K; i < a.size(); i++) {
            if (a.get(i).get(4) != 0d && a.get(i).get(0) == 4) {
                PIPE.add(a.get(i));
            }
        }
        for (int i = 0; i < PIPE.size() - 1; i++) {
            if (!(Double.toString(PIPE.get(i).get(4)).equals(Double.toString(PIPE.get(i + 1).get(4))))) {
                double m, n;
                m = PIPE.get(i).get(4);
                n = PIPE.get(i + 1).get(4);
                m1[(int) m - 1][(int) n - 1]++;
            }
        }
        //计算混合成本
        for (int i = 0; i < c1.length; i++) {
            for (int j = 0; j < c1[0].length; j++) {
                sum = sum + c1[i][j] * m1[i][j];
            }
        }
        return sum;
    }

    /**
     * 计算罐底混合成本
     *
     * @param TKS
     * @param a
     * @param c2
     * @return
     */
    public static double gDimix(Object[][] TKS, List<List<Double>> a, int[][] c2) {
        double[][] m2 = new double[6][6]; //存放各个类型油的混合次数

        // 初始装有的原油类型    TKS格式：容量  原油类型  已有容量 蒸馏塔  供油开始时间 供油结束时间  供油罐编号  混合原油类型集合
        Map<String, List<Integer>> res = new HashMap<>();
        for (int i = 0; i < TKS.length; i++) {
            if (Integer.parseInt(TKS[i][2].toString()) > 0) {
                String key = "TK" + (i + 1);
                if (!res.containsKey(key)) {
                    res.put(key, new ArrayList<>());
                }
                res.get(key).add(Integer.parseInt(TKS[i][1].toString()));
            }
        }

        // 后期转运的原油类型      ODT格式：蒸馏塔号 | 油罐号 | 开始供油时间 | 结束供油时间 | 原油类型
        List<List<Double>> lists = a.stream()
                .filter(e -> e.get(0) == 4 && e.get(4) != 0)        // 过滤出转运记录，排除停运
                .sorted((e1, e2) -> (int) (e1.get(2) - e2.get(2)))  // 按照转运开始时间排序
                .collect(Collectors.toList());
        for (int i = 0; i < lists.size(); i++) {
            int tk = (int) lists.get(i).get(1).doubleValue();
            String key = "TK" + tk;
            if (!res.containsKey(key)) {
                res.put(key, new ArrayList<>());
            }
            res.get(key).add((int) lists.get(i).get(4).doubleValue());
        }

        // 统计各个油罐的使用次数
        for (String key : res.keySet()) {
            List<Integer> oilTypes = res.get(key);
            for (int i = 0; i < oilTypes.size() - 1; i++) {
                int preType = oilTypes.get(i);
                int nextType = oilTypes.get(i + 1);
                if (preType != nextType) {
                    m2[preType - 1][nextType - 1]++;
                }
            }
        }

        // 计算混合成本
        double sum = 0;
        for (int i = 0; i < m2.length; i++) {
            for (int j = 0; j < m2[0].length; j++) {
                sum += m2[i][j] * c2[i][j];
            }
        }
        return sum;
    }

    /**
     * 计算管道的转运能耗
     *
     * @param a           调度计划
     * @param costPerHour 单位时间能耗
     * @param PIPEFR      管道转运速度
     * @return
     */
    public static double gEnergyCost(List<List<Double>> a, double[] costPerHour, double[] PIPEFR) {

        double result = 0.0;

        // 后期转运的原油类型      ODT格式：蒸馏塔号 | 油罐号 | 开始供油时间 | 结束供油时间 | 原油类型  |  转运速度
        List<List<Double>> lists = a.stream()
                .filter(e -> e.get(0) == 4 && e.get(4) != 0)        // 过滤出转运记录，排除停运
                .sorted((e1, e2) -> (int) (e1.get(2) - e2.get(2)))  // 按照转运开始时间排序
                .collect(Collectors.toList());
        for (int i = 0; i < lists.size(); i++) {
            double speed = lists.get(i).get(5).doubleValue();
            int ind = getIndex(PIPEFR, speed);
            double cost = costPerHour[ind];
            double start = lists.get(i).get(2).doubleValue();
            double end = lists.get(i).get(3).doubleValue();
            // 成本 = 单位时间成本 * 总时间
            result += cost * (end - start);
        }

        return Math.round(result * 100.0) / 100.0;
    }

    /**
     * 获取某一个元素所在数组的位置
     *
     * @param arr
     * @param value
     * @return
     */
    public static int getIndex(double[] arr, double value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;//如果未找到返回-1
    }

    public static double sum(double[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
        }
        return sum;
    }

    /**
     * 提取数字
     *
     * @param line
     * @return
     */
    public static List<Double> getNumber(String line) {
        String regEx = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
        Matcher matcher = Pattern.compile(regEx).matcher(line);
        List<String> res = new ArrayList<>();
        while (matcher.find()) {
            String tmp = matcher.group();//tmp为括号中的内容，您可以自己进行下一步的处理
            res.add(tmp);
        }
        return res.stream().mapToDouble(e -> Double.parseDouble(e)).boxed().collect(Collectors.toList());
    }

    public static void main(String[] args) {
        getNumber("This order was 234.35placed for QT3000! OK?").forEach(e -> System.out.println(e));
    }
}
