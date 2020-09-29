package cn.keking;

public class Test {

    public static void main(String[] args) {
        BCryptPasswordEncoder ins = BCryptPasswordEncoder.instance();
        String str = ins.encode("123456");
        System.out.println(str);
    }
}
