package frontend.nodes;

import java.util.ArrayList;

public class FormatString {
    public final ArrayList<String> strs;
    public int formatCharCnt;
    public boolean legal;

    public FormatString(String s) {
        strs = new ArrayList<>();
        formatCharCnt = 0;
        legal = true;
        StringBuilder sb = new StringBuilder();
        if(s!=null) {
            for (int i = 1; i < s.toCharArray().length - 1; i++) {
                char c = s.charAt(i);
                if (c == '%' && i + 1 < s.toCharArray().length - 1 && s.charAt(i + 1) == 'd') {
                    if (sb.toString().length() != 0) { strs.add(sb.toString()); }
                    strs.add("%d");
                    sb = new StringBuilder();
                    i++;
                    formatCharCnt++;
                    continue;
                }
                sb.append(c);
                if ((c == '\\' && i + 1 < s.toCharArray().length - 1 && s.charAt(i + 1) == 'n') ||
                        (c == 32 || c == 33 || (c >= 40 && c <= 126 && c != '\\'))) {
                    continue;
                }
                legal = false;
            }
        }
        if (sb.toString().length() != 0) { strs.add(sb.toString()); }
    }
}
