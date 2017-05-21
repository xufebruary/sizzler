'use strict';

/**
* 获取highcharts本地化配置
* 默认返回日本语言 ja_JP
*/
export function getHighchartsLocalization(locale){
    var localeConfig = HighchartsLocalization();
    if(locale && localeConfig[locale]){
        return localeConfig[locale];
    }else{
        return localization['ja_JP']; // 默认返回日本语言
    }
};

/**
* highcharts国际化配置文件
* 主要涉及时间国际化(datetime类型)
*
*/
var HighchartsLocalization = function(){
    return {
        "ja_JP": {
            startOfWeek: 1, // 0 = Sunday, 1 = Monday. Defaults to 1
            localeLang: {
                weekdays: ['日', '月', '火', '水', '木', '金', '土'],
                months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                shortMonths: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12']
            },
            localeTooltipDateTimeLabelFormats: {
                millisecond: '%Y/%b/%e（%A）, %H:%M:%S.%L',
                second: '%Y/%b/%e（%A）, %H:%M:%S',
                minute: '%Y/%b/%e（%A）, %H:%M',
                hour: '%Y/%b/%e（%A）, %H:%M',
                day: '%Y/%b/%e（%A）',
                week: '%Y/%b/%e（%A）',
                month: '%Y/%b/%e（%A）',
                year: '%Y/%b/%e（%A）'
            },
            localeXAxisDateTimeLabelFormats: {
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%b/%e',
                week: '%b/%e',
                month: '%b/%e',
                year: '%Y'
            },

        },
        "zh_CN": {
            startOfWeek: 1, // 0 = Sunday, 1 = Monday. Defaults to 1
            localeLang: {
                weekdays: ['日', '一', '二', '三', '四', '五', '六'],
                months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                shortMonths: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12']
            },
            localeTooltipDateTimeLabelFormats: {
                millisecond: '%Y/%b/%e（%A）, %H:%M:%S.%L',
                second: '%Y/%b/%e（%A）, %H:%M:%S',
                minute: '%Y/%b/%e（%A）, %H:%M',
                hour: '%Y/%b/%e（%A）, %H:%M',
                day: '%Y/%b/%e（%A）',
                week: '%Y/%b/%e（%A）',
                month: '%Y/%b/%e（%A）',
                year: '%Y/%b/%e（%A）'
            },
            localeXAxisDateTimeLabelFormats: {
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%b/%e',
                week: '%b/%e',
                month: '%b/%e',
                year: '%Y'
            },

        },
        "en_US":{
            startOfWeek: 0, // 0 = Sunday, 1 = Monday. Defaults to 1
            localeLang: {
                weekdays: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
                months: ['January' , 'February' , 'March' , 'April' , 'May' , 'June' , 'July' , 'August' , 'September' , 'October' , 'November' , 'December'],
                shortMonths: ['Jan' , 'Feb' , 'Mar' , 'Apr' , 'May' , 'Jun' , 'Jul' , 'Aug' , 'Sep' , 'Oct' , 'Nov' , 'Dec']
            },
            localeTooltipDateTimeLabelFormats: {
                millisecond: '%A, %b %e, %H:%M:%S.%L',
                second: '%A, %b %e, %H:%M:%S',
                minute: '%A, %b %e, %H:%M',
                hour: '%A, %b %e, %H:%M',
                day: '%A, %b %e, %Y',
                week: 'Week from %A, %b %e, %Y',
                month: '%B %Y',
                year: '%Y'
            },
            localeXAxisDateTimeLabelFormats: {
                millisecond: '%H:%M:%S.%L',
                second: '%H:%M:%S',
                minute: '%H:%M',
                hour: '%H:%M',
                day: '%e. %b',
                week: '%e. %b',
                month: '%b \'%y',
                year: '%Y'
            },

        },

    };

};


