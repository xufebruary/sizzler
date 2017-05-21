/**
 * Created by jianqing on 16/7/28.
 */
/**
 * 数字转字母
 */
function i2s(i) {
    var s = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
    var sArray = s.split(" ");
    if (i < 1) return "";

    if (parseInt((i / 26) + "") == 0) return sArray[i % 26 - 1];
    else {
        if (i % 26 == 0) return (i2s(parseInt((i / 26) + "") - 1)) + sArray[26 - 1];
        else return sArray[parseInt((i / 26) + "") - 1] + sArray[i % 26 - 1];
    }
}

module.exports = i2s;