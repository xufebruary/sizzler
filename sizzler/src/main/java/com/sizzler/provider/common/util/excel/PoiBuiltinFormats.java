package com.sizzler.provider.common.util.excel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ptmind on 2016/1/2.
 */
public class PoiBuiltinFormats {

  /*
   * 0, "General" 1, "0" 2, "0.00" 3, "#,##0" 4, "#,##0.00" 5, "$#,##0_);($#,##0)" 6,
   * "$#,##0_);[Red]($#,##0)" 7, "$#,##0.00);($#,##0.00)" 8, "$#,##0.00_);[Red]($#,##0.00)" 9, "0%"
   * 0xa, "0.00%" 0xb, "0.00E+00" 0xc, "# ?/?" 0xd, "# ??/??" 0xe, "m/d/yy" 0xf, "d-mmm-yy" 0x10,
   * "d-mmm" 0x11, "mmm-yy" 0x12, "h:mm AM/PM" 0x13, "h:mm:ss AM/PM" 0x14, "h:mm" 0x15, "h:mm:ss"
   * 0x16, "m/d/yy h:mm" // 0x17 - 0x24 reserved for international and undocumented 0x25,
   * "#,##0_);(#,##0)" 0x26, "#,##0_);[Red](#,##0)" 0x27, "#,##0.00_);(#,##0.00)" 0x28,
   * "#,##0.00_);[Red](#,##0.00)" 0x29, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)" 0x2a,
   * "_($* #,##0_);_($* (#,##0);_($* \"-\"_);_(@_)" 0x2b,
   * "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)" 0x2c,
   * "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)" 0x2d, "mm:ss" 0x2e, "[h]:mm:ss" 0x2f,
   * "mm:ss.0" 0x30, "##0.0E+0" 0x31, "@" - This is text format. 0x31 "text" - Alias for "@"
   * 
   * //164 "¥"#,##0;[Red]"¥"\-#,##0
   */

  public static Map<String, String> dataFormatCache = new LinkedHashMap<>();
  static {
    dataFormatCache.put("General", "General");
    dataFormatCache.put("0", "0");
    dataFormatCache.put("0.00", "0.00");
    dataFormatCache.put("#,##0", "#,##0");
    dataFormatCache.put("#,##0.00", "#,##0.00");
    // $#,##0_);($#,##0)
    dataFormatCache.put("$#,##0_);($#,##0)", "$#,##0");
    // "$#,##0_);[Red]($#,##0)"
    dataFormatCache.put("$#,##0_);[Red]($#,##0)", "$#,##0");
    // "$#,##0.00);($#,##0.00)"
    dataFormatCache.put("$#,##0.00);($#,##0.00)", "$#,##0.00");
    // "$#,##0.00_);[Red]($#,##0.00)"
    dataFormatCache.put("$#,##0.00_);[Red]($#,##0.00)", "$#,##0.00");
    dataFormatCache.put("0%", "0%");
    dataFormatCache.put("0.00%", "0.00%");
    dataFormatCache.put("0.00E+00", "0.00E+00");
    dataFormatCache.put("# ?/?", "# ?/?");
    dataFormatCache.put("# ??/??", "# ??/??");
    dataFormatCache.put("m/d/yy", "M/d/yyyy");
    // MMM 对应 Dec 格式
    dataFormatCache.put("d-mmm-yy", "d-MMM-yyyy");// 15
    dataFormatCache.put("d-mmm", "d-MMM");// 16
    dataFormatCache.put("mmm-yy", "MMM-yyyy");// 17
    dataFormatCache.put("h:mm AM/PM", "h:mm a");
    dataFormatCache.put("h:mm:ss AM/PM", "h:mm:ss a");
    dataFormatCache.put("h:mm", "H:mm");
    dataFormatCache.put("h:mm:ss", "H:mm:ss");
    dataFormatCache.put("m/d/yy h:mm", "M/d/yyyy h:mm");
    // "#,##0_);(#,##0)"
    dataFormatCache.put("#,##0_);(#,##0)", "#,##0");
    // "#,##0_);[Red](#,##0)"
    dataFormatCache.put("#,##0_);[Red](#,##0)", "#,##0");
    // "#,##0.00_);(#,##0.00)"
    dataFormatCache.put("#,##0.00_);(#,##0.00)", "#,##0.00");
    // "#,##0.00_);[Red](#,##0.00)"
    dataFormatCache.put("#,##0.00_);[Red](#,##0.00)", "#,##0.00");
    // "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)"
    dataFormatCache.put("_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)", " #,##0");
    // "_($* #,##0_);_($* (#,##0);_($* \"-\"_);_(@_)"
    dataFormatCache.put("_($* #,##0_);_($* (#,##0);_($* \"-\"_);_(@_)", "$ #,##0");
    // "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)"
    dataFormatCache.put("_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)", " #,##0.00");
    // "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)"
    dataFormatCache.put("_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)", "$ #,##0.00");
    dataFormatCache.put("mm:ss", "mm:ss");
    dataFormatCache.put("[h]:mm:ss", "H:mm:ss");
    dataFormatCache.put("mm:ss.0", "mm:ss.0");
    dataFormatCache.put("##0.0E+0", "##0.0E+0");
    dataFormatCache.put("@", "text");
    dataFormatCache.put("text", "text");
    // ===非poiapi提供的
    // dataFormatCache.put(164,"¥#,##0");
    // \$#,##0;[Red]\-\$#,##0
    dataFormatCache.put("\\$#,##0;[Red]\\-\\$#,##0", "$#,##0");

    // 时区的自定义相关(中国时区)
    dataFormatCache.put("zh_CN:d-mmm-yy", "d-M月-yyyy");
    dataFormatCache.put("zh_CN:d-mmm", "d-M月");
    dataFormatCache.put("zh_CN:mmm-yy", "M月-yyyy");
    // 增加时间相关的格式
    dataFormatCache.put("hh:mm:ss", "HH:mm:ss");
    dataFormatCache.put("hh:mm", "HH:mm");
    dataFormatCache.put("am/pmhh:mm", "ahh:mm");

    // _(* #,##0.00_);_(* \(#,##0.00\);_(* "-"??_);_(@_)-->fix: #,##0.00
    dataFormatCache.put("_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)", " #,##0.00");
    // _(* #,##0_);_(* \(#,##0\);_(* "-"??_);_(@_)-->fix: #,##0
    dataFormatCache.put("_(* #,##0_);_(* \\(#,##0\\);_(* \"-\"??_);_(@_)", " #,##0");
    // _("$"* #,##0_);_("$"* \(#,##0\);_("$"* "-"??_);_(@_)-->fix:$ #,##0
    dataFormatCache
        .put("_(\"$\"* #,##0_);_(\"$\"* \\(#,##0\\);_(\"$\"* \"-\"??_);_(@_)", "$ #,##0");
    // "¥"#,##0;[Red]"¥"\-#,##0-->fix:¥#,##0
    dataFormatCache.put("\"¥\"#,##0;[Red]\"¥\"\\-#,##0", "¥#,##0");


    // m-d-->fix:M-d
    dataFormatCache.put("m-d", "M-d");
    // yyyy"年"m"月"d"日 "dddd-->fix:yyyy年M月d日 dddd(周) // dddd 暂时不处理
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日 \"dddd", "yyyy年M月d日 EEEE");
    // yyyy"年"m"月"d"日 "ddd-->fix:yyyy年M月d日 ddd
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日 \"ddd", "yyyy年M月d日 E");
    // yyyy"年"m"月"d"日"-->fix:yyyy年M月d日
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日\"", "yyyy年M月d日");
    // yyyy"年"-->fix:yyyy年
    dataFormatCache.put("yyyy\"年\"", "yyyy年");
    // m"月"d"日 "dddd-->fix:M月d日 dddd
    dataFormatCache.put("m\"月\"d\"日 \"dddd", "M月d日 EEEE");
    // m"月"d"日 "ddd-->fix:M月d日 ddd
    dataFormatCache.put("m\"月\"d\"日 \"ddd", "M月d日 E");
    // m"月"-->fix:M月
    dataFormatCache.put("m\"月\"", "M月");
    // d"日"-->fix:d日 ---------
    dataFormatCache.put("d\"日\"", "d日");
    // yyyy-mm-dd-->fix:yyyy-MM-dd
    dataFormatCache.put("yyyy-mm-dd", "yyyy-MM-dd");
    // yyyy-m-d-->fix:yyyy-M-d
    dataFormatCache.put("yyyy-m-d", "yyyy-M-d");
    // yy-mm-dd-->fix:yy-MM-dd
    dataFormatCache.put("yy-mm-dd", "yyyy-MM-dd");
    // mm-dd-->fix:MM-dd
    dataFormatCache.put("mm-dd", "MM-dd");
    // yyyy/mm/dd-->fix:yyyy/MM/dd
    dataFormatCache.put("yyyy/mm/dd", "yyyy/MM/dd");
    // yyyy/m/d-->fix:yyyy/M/d
    dataFormatCache.put("yyyy/m/d", "yyyy/M/d");
    // yy/mm/dd-->fix:yy/MM/dd
    dataFormatCache.put("yy/mm/dd", "yyyy/MM/dd");
    // yy/m/d-->fix:yy/M/d
    dataFormatCache.put("yy/m/d", "yyyy/M/d");
    // mm/dd-->fix:MM/dd
    dataFormatCache.put("mm/dd", "MM/dd");
    // m/d-->fix:M/d
    dataFormatCache.put("m/d", "M/d");
    // yyyy.mm.dd-->fix:yyyy.MM.dd
    dataFormatCache.put("yyyy.mm.dd", "yyyy.MM.dd");
    // yyyy.m.d-->fix:yyyy.M.d
    dataFormatCache.put("yyyy.m.d", "yyyy.M.d");
    // yy.mm.dd-->fix:yy.MM.dd
    dataFormatCache.put("yy.mm.dd", "yyyy.MM.dd");
    // yy.m.d-->fix:yy.M.d
    dataFormatCache.put("yy.m.d", "yyyy.M.d");
    // mm.dd-->fix:MM.dd
    dataFormatCache.put("mm.dd", "MM.dd");
    // m.d-->fix:M.d
    dataFormatCache.put("m.d", "M.d");
    // ///////
    // 上午/下午h"时"mm"分" ---> 上午/下午h时mm分
    dataFormatCache.put("上午/下午h\"时\"mm\"分\"", "ah时mm分");
    // 上午/下午h"时"mm"分"ss"秒" --> 上午/下午h时mm分ss秒
    dataFormatCache.put("上午/下午h\"时\"mm\"分\"ss\"秒\"", "ah时mm分ss秒");
    // yyyy/m/d h:mm--> yyyy/M/d h:mm 小写的h代表12小时制，大写的H代表24小时制
    dataFormatCache.put("yyyy/m/d h:mm", "yyyy/M/d H:mm");
    // h"时"mm"分"-->h时mm分
    dataFormatCache.put("h\"时\"mm\"分\"", "H时mm分");
    // h"时"mm"分"ss"秒"-->h时mm分ss秒
    dataFormatCache.put("h\"时\"mm\"分\"ss\"秒\"", "H时mm分ss秒");

    // AM/PMh"时"mm"分"ss"秒"-->fix:AM/PMh时MM分ss秒
    dataFormatCache.put("AM/PMh\"时\"mm\"分\"ss\"秒\"", "a h时mm分ss秒");

    // AM/PMh"时"mm"分"-->fix:ah时MM分
    dataFormatCache.put("AM/PMh\"时\"mm\"分\"", "ah时mm分");
    // AM/PMh"时"-->fix:ah时
    dataFormatCache.put("AM/PMh\"时\"", "ah时");
    // AM/PMh:mm:ss-->fix:ah:mm:ss
    dataFormatCache.put("AM/PMh:mm:ss", "ah:mm:ss");
    // AM/PMh:mm-->fix:ah:mm
    dataFormatCache.put("AM/PMh:mm", "ah:mm");
    // h"时"-->fix:h时
    dataFormatCache.put("h\"时\"", "H时");
    // am/pmhh:mm:ss-->fix:ahh:mm:ss
    dataFormatCache.put("am/pmhh:mm:ss", "ahh:mm:ss");
    // hh":"mmam/pm-->fix:hh:mmam/pm
    dataFormatCache.put("hh\":\"mmam/pm", "hh:mma");
    // //hh":"mmAM/PM-->fix:hh:mma
    dataFormatCache.put("hh\":\"mmAM/PM", "hh:mma");

    // 货币
    // [$￥-804] #,##0.00-->fix:￥#,##0.00
    dataFormatCache.put("[$￥-804] #,##0.00", "￥#,##0.00");
    // [$$-409] #,##0.00-->fix:$#,##0.00
    dataFormatCache.put("[$$-409] #,##0.00", "$#,##0.00");
    // [$¥-411] #,##0.00-->fix:$¥#,##0.00
    dataFormatCache.put("[$¥-411] #,##0.00", "¥#,##0.00");

    // [$¥]#,##0.00-->fix:¥#,##0.00
    dataFormatCache.put("[$¥]#,##0.00", "¥#,##0.00");
    dataFormatCache.put("[$$]#,##0.00", "$#,##0.00");
    dataFormatCache.put("[$￥]#,##0.00", "￥#,##0.00");

    // [$RMB¥]#,##0.00-->fix:RMB¥#,##0.00
    dataFormatCache.put("[$RMB¥]#,##0.00", "RMB¥#,##0.00");
    // [$CNY ¥]#,##0.00-->fix:CNY ¥#,##0.00
    dataFormatCache.put("[$CNY ¥]#,##0.00", "CNY ¥#,##0.00");
    // #,##0.00[$¥]-->fix:#,##0.00¥
    dataFormatCache.put("#,##0.00[$¥]", "#,##0.00¥");
    // #,##0.00[$$]-->fix:#,##0.00[$$]
    dataFormatCache.put("#,##0.00[$$]", "#,##0.00$");
    // [$US$]#,##0.00-->fix:US$#,##0.00
    dataFormatCache.put("[$US$]#,##0.00", "US$#,##0.00");
    // [$USD $]#,##0.00-->fix:USD $#,##0.00
    dataFormatCache.put("[$USD $]#,##0.00", "USD $#,##0.00");
    // [$JP¥]#,##0-->fix:JP¥#,##0
    dataFormatCache.put("[$JP¥]#,##0", "JP¥#,##0");
    // [$JPY ¥]#,##0-->fix:JPY ¥#,##0
    dataFormatCache.put("[$JPY ¥]#,##0", "JPY ¥#,##0");


    // \$#,##0_);[Red]\(\$#,##0\)-->fix:$#,##0
    dataFormatCache.put("\\$#,##0_);[Red]\\(\\$#,##0\\)", "$#,##0");
    // 0.00_);[Red]\(0.00\)-->fix:0.00
    dataFormatCache.put("0.00_);[Red]\\(0.00\\)", "0.00");
    // #,##0;"-"#,##0-->fix:#,##0
    dataFormatCache.put("#,##0;\"-\"#,##0", "#,##0");
    // 0.00" "-->fix:0.00
    dataFormatCache.put("0.00\" \"", "0.00");
    // #,##0.00;[Red]#,##0.00-->fix:#,##0.00
    dataFormatCache.put("#,##0.00;[Red]#,##0.00", "#,##0.00");
    // 0" "-->fix:0
    dataFormatCache.put("0\" \"", "0");
    // 0.000_);[Red]\(0.000\)-->fix:0.000
    dataFormatCache.put("0.000_);[Red]\\(0.000\\)", "0.000");
    // _-* #,##0.0_-;\-* #,##0.0_-;_-* "-"??_-;_-@_--->fix:#,##0.0
    dataFormatCache.put("_-* #,##0.0_-;\\-* #,##0.0_-;_-* \"-\"??_-;_-@_", "#,##0.0");
    // _-* #,##0.00_-;\-* #,##0.00_-;_-* "-"??_-;_-@_--->fix:#,##0.00
    dataFormatCache.put("_-* #,##0.00_-;\\-* #,##0.00_-;_-* \"-\"??_-;_-@_", "#,##0.00");
    // 0.0-->fix:0.0
    dataFormatCache.put("0.0", "0.0");
    // 0.0%-->fix:0.0%
    dataFormatCache.put("0.0%", "0.0%");
    // _-* #,##0_-;\-* #,##0_-;_-* "-"??_-;_-@_--->fix:#,##0
    dataFormatCache.put("_-* #,##0_-;\\-* #,##0_-;_-* \"-\"??_-;_-@_", "#,##0");
    // _-* #,##0.0000_-;\-* #,##0.0000_-;_-* "-"??_-;_-@_--->fix:#,##0.0000
    dataFormatCache.put("_-* #,##0.0000_-;\\-* #,##0.0000_-;_-* \"-\"??_-;_-@_", "#,##0.0000");
    // _-* #,##0.00000_-;\-* #,##0.00000_-;_-* "-"??_-;_-@_--->fix:#,##0.00000
    dataFormatCache.put("_-* #,##0.00000_-;\\-* #,##0.00000_-;_-* \"-\"??_-;_-@_", "#,##0.00000");
    // m/d;@-->fix:M/d
    dataFormatCache.put("m/d;@", "M/d");
    // _-* #,##0.0_-;\-* #,##0.0_-;_-* "-"??_-;_-@_--->fix:#,##0.0
    dataFormatCache.put("_-* #,##0.0_-;\\-* #,##0.0_-;_-* \"-\"??_-;_-@_", "#,##0.0");

    // M/d/yyyy
    dataFormatCache.put("M/d/yyyy", "M/d/yyyy");
    // m/d/yyyy h:mm:ss
    dataFormatCache.put("m/d/yyyy h:mm:ss", "M/d/yyyy h:mm:ss");

    // andy add 2016-06-29
    dataFormatCache.put("\"\"MMMM\" de \"yyyy\"\"", "MMMM de yyyy");
    dataFormatCache.put("\"$ \"#,##0.00", "$ #,##0.00");
    dataFormatCache.put("\"$\"#,##0 ;\"$\"(#,##0)", "$#,##0");
    dataFormatCache.put("\"$\"#,##0", "$#,##0");
    dataFormatCache.put("\"$\"#,##0.00", "$#,##0.00");
    dataFormatCache.put("\"$\"#,##0.000", "$#,##0.000");
    dataFormatCache.put("\"$\"#,##0.00;[Red]\\-\"$\"#,##0.00", "$#,##0.00");
    dataFormatCache.put("\"$\"#,##0.00_);[Red]\\(\"$\"#,##0.00\\)", "$#,##0.00");
    dataFormatCache.put("\"$\"#,##0;[Red]\\-\"$\"#,##0", "$#,##0");
    dataFormatCache.put("\"R$\"#,##0.00", "R$#,##0.00");
    dataFormatCache.put("\"S\"G\"D\" #,##0.00", "SGD #,##0.00");
    dataFormatCache.put("\"US$\"#,##0.00;\\-\"US$\"#,##0.00", "US$#,##0.00");
    dataFormatCache.put("\"US$\"#,##0.00_);\\(\"US$\"#,##0.00\\)", "US$#,##0.00");
    dataFormatCache.put("\"£ \"#,##0.00", "£ #,##0.00");
    dataFormatCache.put("\"£\"#,##0", "£#,##0");
    dataFormatCache.put("\"£\"#,##0.00", "£#,##0.00");
    dataFormatCache.put("\"¥\"#,##0", "¥#,##0");
    dataFormatCache.put("\"¥\"#,##0.00", "¥#,##0.00");
    dataFormatCache.put("\"¥\"#,##0.00;\"¥\"\\-#,##0.00", "¥#,##0.00");
    dataFormatCache.put("\"¥\"#,##0.00;[Red]\"¥\"\\-#,##0.00", "¥#,##0.00");
    dataFormatCache.put("\"￥\"#,##0.000_);[Red]\\(\"￥\"#,##0.000\\)", "￥#,##0.000");
    dataFormatCache.put("#,###", "#,###");
    dataFormatCache.put("#,##0 €", "#,##0 €");
    dataFormatCache.put("#,##0\"$\"", "#,##0$");
    dataFormatCache.put("#,##0\"¥\"", "#,##0¥");
    dataFormatCache.put("#,##0\"€\"", "#,##0€");
    dataFormatCache.put("#,##0,;\\▲#,##0,", "#,##0,");
    dataFormatCache.put("#,##0.###############", "#,##0.###############");
    dataFormatCache.put("#,##0.0", "#,##0.0");
    dataFormatCache.put("#,##0.00\" €\"", "#,##0.00 €");
    dataFormatCache.put("#,##0.00\"¥\"", "#,##0.00¥");
    dataFormatCache.put("#,##0.00\"€\"", "#,##0.00€");
    dataFormatCache.put("#,##0.000", "#,##0.000");
    dataFormatCache.put("#,##0.00;(#,##0.00)", "#,##0.00");
    dataFormatCache.put("#,##0.00\\ [$€-1]", "#,##0.00 €");
    dataFormatCache.put("#,##0.00_ ", "#,##0.00");
    dataFormatCache.put("#,##0.00_ ;\\-#,##0.00\\ ", "#,##0.00");
    dataFormatCache.put("#,##0.0;[Red]\\-#,##0.0", "#,##0.0");
    dataFormatCache.put("#,##0;(#,##0);\"-\"", "#,##0");
    dataFormatCache.put("#,##0;\\-#,##0", "#,##0");
    dataFormatCache.put("#,##0\\ \"€\"", "#,##0 €");
    dataFormatCache.put("#,##0\\ [$€-1]", "#,##0 €");
    dataFormatCache.put("#,##0_ ", "#,##0");
    dataFormatCache.put("#,##0_ ;\\-#,##0\\ ", "#,##0");
    dataFormatCache.put("$#,##0.00", "$#,##0.00");
    dataFormatCache.put("0.000", "0.000");
    dataFormatCache.put("0.0000%", "0.0000%");
    dataFormatCache.put("0.0000000", "0.0000000");
    dataFormatCache.put("0.00000;[Red]0.00000", "0.00000");
    dataFormatCache.put("0.00000E+00", "0.00000E+00");
    dataFormatCache.put("0.0000E+00", "0.0000E+00");
    dataFormatCache.put("0.0000_ ", "0.0000");
    dataFormatCache.put("0.000;[Red]0.000", "0.000");
    dataFormatCache.put("0.000E+00", "0.000E+00");
    dataFormatCache.put("0.00;[Red]0.00", "0.00");
    dataFormatCache.put("0.00_ ", "0.00");
    dataFormatCache.put("0.0;[Red]0.0", "0.0");
    dataFormatCache.put("0.0\\ ;\\-0.0\\ ;0.0\\ ;@\\ ", "0.0");
    dataFormatCache.put("0.0_);[Red]\\(0.0\\)", "0.0");
    dataFormatCache.put("00\".\"000\".\"000\"/\"0000\"-\"00", "00.000.000/0000-00");
    dataFormatCache.put("00.00", "00.00");
    dataFormatCache.put("0;[Red]0", "0");
    dataFormatCache.put("0_ ", "0");
    dataFormatCache.put("0_);[Red]\\(0\\)", "0");
    dataFormatCache.put("D/M/YYYY", "D/M/YYYY");
    dataFormatCache.put("H:mm:ss", "H:mm:ss");
    dataFormatCache.put("HH:MM", "HH:MM");
    dataFormatCache.put("M/d", "M/d");
    dataFormatCache.put("MMM d, yyyy h:mm AM/PM", "MMM d, yyyy h:mm a");
    dataFormatCache.put("MMMM d, yyyy", "MMMM d, yyyy");
    dataFormatCache.put("[$$]#,##0", "[$$]#,##0");
    dataFormatCache.put("[$-1010409]#,##0;\\-#,##0", "#,##0");
    dataFormatCache.put("[$-409]d\\-mmm", "d-MMM");
    dataFormatCache.put("[$-409]d\\-mmm;@", "d-MMM");
    dataFormatCache.put("[$-409]d\\-mmm\\-yy;@", "d-MMM-yyyy");
    dataFormatCache.put("[$-409]dd\\-mmm\\-yy", "dd-MMM-yyyy");
    dataFormatCache.put("[$-409]h:mm:ss\\ AM/PM;@", "h:mm:ss a");
    dataFormatCache.put("[$-409]h:mm\\ AM/PM;@", "h:mm a");
    dataFormatCache.put("[$-409]mmm\\-yy", "MMM-yyyy");
    dataFormatCache.put("[$-409]mmm\\-yy;@", "MMM-yyyy");
    dataFormatCache.put("[$-409]mmmm\\ d\\,\\ yyyy", "MMMM d, yyyy");
    dataFormatCache.put("[$-409]mmmm\\-yy;@", "MMMM-yyyy");
    dataFormatCache.put("[$-409]mmmmm;@", "MMMMM");
    dataFormatCache.put("[$-409]mmmmm\\-yy;@", "MMMMM-yyyy");
    dataFormatCache.put("[$-409]yyyy/m/d\\ h:mm\\ AM/PM;@", "yyyy/M/d h:mm a");
    dataFormatCache.put("[$-804]aaa;@", "aaa");
    dataFormatCache.put("[$-804]aaaa;@", "aaaa");
    dataFormatCache.put("[$-C0A]mmm\\-yy;@", "MMM-yyyy");
    dataFormatCache.put("[$-F400]h:mm:ss\\ AM/PM", "h:mm:ss a");
    dataFormatCache.put("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy", "dddd, MMMM dd, yyyy");
    dataFormatCache.put("[$R$ -416]#,##0.00", "R$ #,##0.00");
    dataFormatCache.put("[$£-809]#,##0", "£#,##0");
    dataFormatCache.put("[$£-809]#,##0.00", "£#,##0.00");
    dataFormatCache.put("[$£-809]#,##0.00;-[$£-809]#,##0.00", "#,##0.00");
    dataFormatCache.put("[$£]#,##0", "£#,##0");
    dataFormatCache.put("[$£]#,##0.00", "£#,##0.00");
    dataFormatCache.put("[$¥-411]#,##0", "#,##0");
    dataFormatCache.put("[$¥]#,##0", "¥#,##0");
    dataFormatCache.put("[$฿]#,##0", "฿#,##0");
    dataFormatCache.put("[$₪-40D]#,##0", "₪#,##0");
    dataFormatCache.put("[$₪-40D]#,##0.00", "₪#,##0.00");
    dataFormatCache.put("[$₪]#,##0", "₪#,##0");
    dataFormatCache.put("[$€-809]#,##0.00;-[$€-809]#,##0.00", "#,##0.00");
    dataFormatCache.put("[$￥-804]#,##0.00", "￥#,##0.00");
    dataFormatCache.put("[DBNum1][$-804]h\"时\"mm\"分\";@", "h时mm分");
    dataFormatCache.put("[DBNum1][$-804]m\"月\"d\"日\";@", "M月d日");
    dataFormatCache.put("[DBNum1][$-804]yyyy\"年\"m\"月\";@", "yyyy年M月");
    dataFormatCache.put("[DBNum1][$-804]yyyy\"年\"m\"月\"d\"日\";@", "yyyy年M月d日");
    dataFormatCache.put("[DBNum1][$-804]上午/下午h\"时\"mm\"分\";@", "上午/下午h时mm分");
    dataFormatCache.put("\\$#,##0.00;\\-\\$#,##0.00", "$#,##0.00");
    dataFormatCache.put("\\$#,##0.00_);[Red]\\(\\$#,##0.00\\)", "$#,##0.00");
    dataFormatCache.put("_ \"¥\"* #,##0_ ;_ \"¥\"* \\-#,##0_ ;_ \"¥\"* \"-\"_ ;_ @_ ", " ¥ #,##0");
    dataFormatCache.put("_ * #,##0_ ;_ * \\-#,##0_ ;_ * \"-\"??_ ;_ @_ ", "  #,##0");
    dataFormatCache.put("_ * #,##0_ ;_ * \\-#,##0_ ;_ * \"-\"_ ;_ @_ ", "  #,##0");
    dataFormatCache.put("_(\"$\"* #,##0.00_);_(\"$\"* \\(#,##0.00\\);_(\"$\"* \"-\"??_);_(@_)",
        "$ #,##0.00");
    dataFormatCache.put("_(\"$\"* #,##0_);_(\"$\"* (#,##0);_(\"$\"* \"-\"_);_(@_)", "$ #,##0");
    dataFormatCache.put("_(\"€\"* #,##0.00_);_(\"€\"* \\(#,##0.00\\);_(\"€\"* \"-\"??_);_(@_)",
        "€ #,##0.00");
    dataFormatCache
        .put("_(\"€\"* #,##0_);_(\"€\"* \\(#,##0\\);_(\"€\"* \"-\"??_);_(@_)", "€ #,##0");
    dataFormatCache.put("_(* #,##0.00000_);_(* \\(#,##0.00000\\);_(* \"-\"??_);_(@_)",
        " #,##0.00000");
    dataFormatCache.put(
        "_([$€-2]\\ * #,##0.00_);_([$€-2]\\ * \\(#,##0.00\\);_([$€-2]\\ * \"-\"??_);_(@_)",
        "€  #,##0.00");
    dataFormatCache.put(
        "_([$€-2]\\ * #,##0.0_);_([$€-2]\\ * \\(#,##0.0\\);_([$€-2]\\ * \"-\"??_);_(@_)",
        "€  #,##0.0");
    dataFormatCache.put(
        "_([$€-2]\\ * #,##0_);_([$€-2]\\ * \\(#,##0\\);_([$€-2]\\ * \"-\"??_);_(@_)", "€  #,##0");
    dataFormatCache.put("_-\"$\"* #,##0.00_-;\\-\"$\"* #,##0.00_-;_-\"$\"* \"-\"??_-;_-@",
        "$ #,##0.00");
    dataFormatCache
        .put("_-\"€\"\\ * #,##0.00_-;_-\"€\"\\ * #,##0.00\\-;_-\"€\"\\ * \"-\"??_-;_-@_-",
            "€  #,##0.00");
    dataFormatCache.put("_-\"€\"\\ * #,##0_-;_-\"€\"\\ * #,##0\\-;_-\"€\"\\ * \"-\"??_-;_-@_-",
        "€  #,##0");
    dataFormatCache.put(
        "_-* #,##0.00000000\\ _€_-;\\-* #,##0.00000000\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.00000000 €");
    dataFormatCache.put(
        "_-* #,##0.0000000\\ _€_-;\\-* #,##0.0000000\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.0000000 €");
    dataFormatCache.put(
        "_-* #,##0.000000\\ _€_-;\\-* #,##0.000000\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.000000 €");
    dataFormatCache.put("_-* #,##0.00000_-;\\-* #,##0.00000_-;_-* \"-\"??_-;_-@_-", " #,##0.00000");
    dataFormatCache.put("_-* #,##0.0000_-;\\-* #,##0.0000_-;_-* \"-\"??_-;_-@_-", " #,##0.0000");
    dataFormatCache.put("_-* #,##0.000\\ _€_-;\\-* #,##0.000\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.000 €");
    dataFormatCache.put("_-* #,##0.00\\ _€_-;\\-* #,##0.00\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.00 €");
    dataFormatCache.put("_-* #,##0.00_-;\\-* #,##0.00_-;_-* \"-\"??_-;_-@", " #,##0.00");
    dataFormatCache.put("_-* #,##0.00_-;\\-* #,##0.00_-;_-* \"-\"??_-;_-@_-", " #,##0.00");
    dataFormatCache.put("_-* #,##0.00_-;_-* #,##0.00\\-;_-* \"-\"??_-;_-@_-", " #,##0.00");
    dataFormatCache.put("_-* #,##0.0\\ _€_-;\\-* #,##0.0\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-",
        " #,##0.0 €");
    dataFormatCache.put("_-* #,##0.0\\ _€_-;\\-* #,##0.0\\ _€_-;_-* \"-\"?\\ _€_-;_-@_-",
        " #,##0.0 €");
    dataFormatCache.put("_-* #,##0.0_-;\\-* #,##0.0_-;_-* \"-\"??_-;_-@", " #,##0.0");
    dataFormatCache.put("_-* #,##0.0_-;\\-* #,##0.0_-;_-* \"-\"??_-;_-@_-", " #,##0.0");
    dataFormatCache.put("_-* #,##0\\ _€_-;\\-* #,##0\\ _€_-;_-* \"-\"??\\ _€_-;_-@_-", " #,##0 €");
    dataFormatCache.put("_-* #,##0\\ _€_-;\\-* #,##0\\ _€_-;_-* \"-\"\\ _€_-;_-@_-", " #,##0 €");
    dataFormatCache.put("_-* #,##0_-;\\-* #,##0_-;_-* \"-\"??_-;_-@", " #,##0");
    dataFormatCache.put("_-* #,##0_-;\\-* #,##0_-;_-* \"-\"??_-;_-@_-", " #,##0");
    dataFormatCache.put("_-* #,##0_-;_-* #,##0\\-;_-* \"-\"??_-;_-@_-", " #,##0");
    dataFormatCache.put(
        "_-[$$-409]* #,##0.00_ ;_-[$$-409]* \\-#,##0.00\\ ;_-[$$-409]* \"-\"??_ ;_-@_ ",
        "$ #,##0.00");
    dataFormatCache.put(
        "_-[$€-2]\\ * #,##0.00_-;_-[$€-2]\\ * #,##0.00\\-;_-[$€-2]\\ * \"-\"??_-;_-@_-",
        "€  #,##0.00");
    dataFormatCache.put("d mmm", "d MMM");
    dataFormatCache.put("d mmmm, yyyy", "d MMMM, yyyy");
    dataFormatCache.put("d mmmm", "d MMMM");
    dataFormatCache.put("d mmmm,yyyy", "d MMMM,yyyy");
    dataFormatCache.put("d\" \"mmm\" \"yy", "d MMM yyyy");
    dataFormatCache.put("d\" \"mmmm\" \"yyyy", "d MMMM yyyy");
    dataFormatCache.put("d\"-\"m\"-\"yyyy\"  \"", "d-M-yyyy");
    dataFormatCache.put("d\"-\"m\"-\"yyyy\"  \"", "d-M-yyyy  ");
    dataFormatCache.put("d\"-\"mmm\"-\"yyyy", "d-MMM-yyyy");
    dataFormatCache.put("d\"-\"mmm", "d-MMM");
    dataFormatCache.put("d, mmmm", "d, MMMM");
    dataFormatCache.put("d", "d");
    dataFormatCache.put("d-MMM", "d-MMM");
    dataFormatCache.put("d-m", "d-M");
    dataFormatCache.put("d-mmmm", "d-MMMM");
    dataFormatCache.put("d-mmmm-yy", "d-MMMM-yyyy");
    dataFormatCache.put("d/MM/yyyy", "d/MM/yyyy");
    dataFormatCache.put("d/m", "d/M");
    dataFormatCache.put("d/m/yyyy", "d/M/yyyy");
    dataFormatCache.put("dd\"/\"mm\"/\"yyyy", "dd/MM/yyyy");
    dataFormatCache.put("dd-mmm-yy", "dd-MMM-yyyy");
    dataFormatCache.put("dd-mmmm-yy", "dd-MMMM-yyyy");
    dataFormatCache.put("dd.MM.yyyy", "dd.MM.yyyy");
    dataFormatCache.put("dd.mm.yyyy", "dd.MM.yyyy");
    dataFormatCache.put("dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss");
    dataFormatCache.put("dd/MM/yyyy", "dd/MM/yyyy");
    dataFormatCache.put("dd/mm", "dd/MM");
    dataFormatCache.put("dd/mm/yy", "dd/MM/yyyy");
    dataFormatCache.put("dd/mm/yyyy", "dd/MM/yyyy");
    dataFormatCache.put("dd/mm/yyyy;@", "dd/MM/yyyy");
    dataFormatCache.put("ddd\", \"mmm\" \"d\", \"yyyy", "ddd, MMM d, yyyy");
    dataFormatCache.put("ddd\\,\\ mmmm\\ dd", "ddd, MMMM dd");
    dataFormatCache.put("dddd\", \"d\" \"mmmm\" \"yyyy", "dddd, d MMMM yyyy");
    dataFormatCache.put("dddd\", \"mmmm\" \"d\"  \"", "dddd, MMMM d ");
    dataFormatCache.put("dddd\", \"mmmm\" \"d\"  \"", "dddd, MMMM d  ");
    dataFormatCache.put("dddd\", \"mmmm\" \"d\", \"", "dddd, MMMM d, ");
    dataFormatCache.put("dddd\", \"mmmm\" \"d", "dddd, MMMM d");
    dataFormatCache.put("dddd, MMMM d, yyyy", "dddd, MMMM d, yyyy");
    dataFormatCache.put("dmmm", "dMMM");
    dataFormatCache.put("h\":\"mm\" \"a/p", "h:mm a");
    dataFormatCache.put("h\":\"mm\":\"ss\" \"A/P", "h:mm:ss a");
    dataFormatCache.put("h\":\"mm", "h:mm");
    dataFormatCache.put("h\"时\"mm\"分\";@", "h时mm分");
    dataFormatCache.put("h\"时\"mm\"分\"ss\"秒\";@", "h时mm分ss秒");
    dataFormatCache.put("h\"時\"mm\"分\";@", "h時mm分");
    dataFormatCache.put("h\"時\"mm\"分\"ss\"秒\";@", "h時mm分ss秒");
    dataFormatCache.put("h:mm am/pm", "h:mm a");
    dataFormatCache.put("h:mm:ss am/pm", "h:mm:ss a");
    dataFormatCache.put("h:mm:ss;@", "h:mm:ss");
    dataFormatCache.put("h:mm;@", "h:mm");
    dataFormatCache.put("hh\":\"mm\":\"ssam/pm", "hh:mm:ssa");
    dataFormatCache.put("hh\":\"mm", "hh:mm");
    dataFormatCache.put("m\"-\"d\"-\"yy", "M-d-yyyy");
    dataFormatCache.put("m\"-\"d\"-\"yyyy", "M-d-yyyy");
    dataFormatCache.put("m\".\"d\".\"yy", "M.d.yyyy");
    dataFormatCache.put("m\".\"d\".\"yyyy\" \"h\":\"m\":\"s", "M.d.yyyy h:m:s");
    dataFormatCache.put("m\".\"d\".\"yyyy", "M.d.yyyy");
    dataFormatCache.put("m\"/\"d\"(\"ddd\")\"", "M/dddd");
    dataFormatCache.put("m\"/\"d\"/\"yy\" - \"ddd", "M/d/yyyy - ddd");
    dataFormatCache.put("m\"/\"d\"/\"yy", "M/d/yyyy");
    dataFormatCache.put("m\"/\"d\"/\"yyyy", "M/d/yyyy");
    dataFormatCache.put("m\"月\"d\"日\"", "M月d日");
    dataFormatCache.put("m\"月\"d\"日\";@", "M月d日");
    dataFormatCache.put("m-d-yyyy", "M-d-yyyy");
    dataFormatCache.put("m/d/yy;@", "M/d/yyyy");
    dataFormatCache.put("m/d/yyyy", "M/d/yyyy");
    dataFormatCache.put("mm\"-\"dd\"-\"yyyy\" \"h\":\"m\":\"s", "MM-dd-yyyy h:m:s");
    dataFormatCache.put("mm\".\"dd\".\"yy\" \"h\":\"m\":\"s", "MM.dd.yyyy h:m:s");
    dataFormatCache.put("mm\".\"dd\".\"yyyy\" \"h\":\"m\":\"s", "MM.dd.yyyy h:m:s");
    dataFormatCache.put("mm\"/\"d\"/\"yyyy", "MM/d/yyyy");
    dataFormatCache.put("mm\"/\"dd\"/\"yyyy\" \"h\":\"m\":\"s", "MM/dd/yyyy h:m:s");
    dataFormatCache.put("mm\"/\"dd\"/\"yyyy", "MM/dd/yyyy");
    dataFormatCache.put("mm\"/\"dd", "MM/dd");
    dataFormatCache.put("mm/dd/yy", "MM/dd/yyyy");
    dataFormatCache.put("mm/dd/yy;@", "MM/dd/yyyy");
    dataFormatCache.put("mm/dd/yyyy", "MM/dd/yyyy");
    dataFormatCache.put("mm/yyyy", "MM/yyyy");
    dataFormatCache.put("mm\\-dd\\-yy", "MM-dd-yyyy");
    dataFormatCache.put("mm\\-dd\\-yyyy", "MM-dd-yyyy");
    dataFormatCache.put("mm\\.dd", "MM.dd");
    dataFormatCache.put("mm\\.dd\\.yy", "MM.dd.yyyy");
    dataFormatCache.put("mm\\.dd\\.yyyy", "MM.dd.yyyy");
    dataFormatCache.put("mmm d", "MMM d");
    dataFormatCache.put("mmm\" \"", "MMM ");
    dataFormatCache.put("mmm\" , \"yyyy", "MMM , yyyy");
    dataFormatCache.put("mmm\", \"yyyy", "MMM, yyyy");
    dataFormatCache.put("mmm\"-\"yyyy", "MMM-yyyy");
    dataFormatCache.put("mmm", "MMM");
    dataFormatCache.put("mmm-d", "MMM-d");
    dataFormatCache.put("mmm\\-yyyy", "MMM-yyyy");
    dataFormatCache.put("mmmm / yyyy", "MMMM / yyyy");
    dataFormatCache.put("mmmm d, yyyy", "MMMM d, yyyy");
    dataFormatCache.put("mmmm d", "MMMM d");
    dataFormatCache.put("mmmm yyyy", "MMMM yyyy");
    dataFormatCache.put("mmmm\" \"d\", \"yyyy", "MMMM d, yyyy");
    dataFormatCache.put("mmmm\" \"yyyy", "MMMM yyyy");
    dataFormatCache.put("mmmm-d", "MMMM-d");
    dataFormatCache.put("mmmm/d", "MMMM/d");
    dataFormatCache.put("reserved-0x1e", "reserved-0x1e");
    dataFormatCache.put("reserved-0x1f", "reserved-0x1f");
    dataFormatCache.put("reserved-0x20", "reserved-0x20");
    dataFormatCache.put("reserved-0x21", "reserved-0x21");
    dataFormatCache.put("yy\"-\"mm\"-\"dd\"  \"h\":\"m\":\"s", "yyyy-MM-dd h:m:s");
    dataFormatCache.put("yy\".\"mm\".\"dd\"  \"h\":\"m\":\"s", "yyyy.MM.dd h:m:s");
    dataFormatCache.put("yy\"/\"mm\"/\"dd\"  \"h\":\"m\":\"s", "yyyy/MM/dd h:m:s");
    dataFormatCache.put("yy\"年\"m\"月\"", "yyyy年M月");
    dataFormatCache.put("yyyy\"-\"m\"-\"d\" \"h\":\"m\":\"s", "yyyy-M-d h:m:s");
    dataFormatCache.put("yyyy\"-\"m\"-\"d\" \"hh\":\"mm\":\"ss", "yyyy-M-d hh:mm:ss");
    dataFormatCache.put("yyyy\"-\"mm\"-\"dd\"  \"h\":\"m\":\"s", "yyyy-MM-dd h:m:s");
    dataFormatCache.put("yyyy\"-\"mm\"-\"dd\" \"hh\":\"mm\":\"ss", "yyyy-MM-dd hh:mm:ss");
    dataFormatCache.put("yyyy\"-\"mm\"-\"dd\" \"", "yyyy-MM-dd ");
    dataFormatCache.put("yyyy\"-\"mm\"-\"dd", "yyyy-MM-dd");
    dataFormatCache.put("yyyy\".\"mm\".\"dd\"  \"h\":\"m\":\"s", "yyyy.MM.dd h:m:s");
    dataFormatCache.put("yyyy\".\"mm\".\"dd\" \"", "yyyy.MM.dd ");
    dataFormatCache.put("yyyy\"/\"mm\"/\"dd\"  \"h\":\"m\":\"s", "yyyy/MM/dd h:m:s");
    dataFormatCache.put("yyyy\"年\"m\"月\";@", "yyyy年M月");
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日 \"h\"時\"mm\"分\"ss\"秒\"", "yyyy年M月d日 h時mm分ss秒");
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日\"h\":\"mm\":\"ss", "yyyy年m月d日h:mm:ss");
    dataFormatCache.put("yyyy\"年\"m\"月\"d\"日\"h\"时\"mm\"分\"ss\"秒\"", "yyyy年M月d日h时mm分ss秒");
    dataFormatCache.put("yyyy-M-d", "yyyy-M-d");
    dataFormatCache.put("yyyy-MM-dd", "yyyy-MM-dd");
    dataFormatCache.put("yyyy-mm", "yyyy-MM");
    dataFormatCache.put("yyyy-mm-dd h:mm:ss", "yyyy-MM-dd h:mm:ss");
    dataFormatCache.put("yyyy-mm-dd hh:mm:ss", "yyyy-MM-dd hh:mm:ss");
    dataFormatCache.put("yyyy/mm/dd\\ hh:mm:ss", "yyyy-MM-dd hh:mm:ss");
    dataFormatCache.put("yyyy/m/d\\ hh:mm:ss", "yyyy-M-d hh:mm:ss");
    dataFormatCache.put("yyyy. M. d", "yyyy. M. d");
    dataFormatCache.put("yyyy.m", "yyyy.M");
    dataFormatCache.put("yyyy/MM/dd H:mm:ss", "yyyy/MM/dd H:mm:ss");
    dataFormatCache.put("yyyy/MM/dd", "yyyy/MM/dd");
    dataFormatCache.put("yyyy/m", "yyyy/M");
    dataFormatCache.put("yyyy/m/d;@", "yyyy/M/d");
    dataFormatCache.put("yyyy/m/d\\ h:mm;@", "yyyy/M/d h:mm");
    dataFormatCache.put("yyyy/mm", "yyyy/MM");
    dataFormatCache.put("yyyy\\-m\\-d\\ hh:mm", "yyyy-M-d hh:mm");
    dataFormatCache.put("yyyy\\-mm\\-dd", "yyyy-MM-dd");
    dataFormatCache.put("yyyymmdd\"  \"h\":\"m\":\"s", "yyyyMMdd h:m:s");
    dataFormatCache.put("yyyymmdd\" \"", "yyyyMMdd ");
    dataFormatCache.put("上午/下午h\"时\"mm\"分\";@", "上午/下午h时mm分");
    dataFormatCache.put("上午/下午h\"时\"mm\"分\"ss\"秒\";@", "上午/下午h时mm分ss秒");
  }

  public static String getDataFormat(String index) {
    return dataFormatCache.get(index);
  }

  public static void addDataFormat(String index, String dataFormat) {
    dataFormatCache.put(index, dataFormat);
  }
}
