export default {
	getDataType: function (dataType) {
		if (["DOUBLE", "FLOAT", "INTEGER", "LONG", "NUMBER"].indexOf(dataType) != -1) {
			return "NUMBER";
		}
		return dataType;
	},
	formatDataViewer: function (data, unit, dataType, dataFormat, notSplitNumber) {

		var reg = /^(\-|\+)?\d+(\.\d+)?$/; //判断字符串是否为数字(正数、负数、小数) //var reg = /^[0-9]+.?[0-9]*$/;
		if (!reg.test(data)) {
			return data; // 如果不是数值直接将data返回，unit抛弃（table中的维度列）
		}
		data = parseFloat(data);
		unit = unit || '';
		var result = ''; // 默认将数值和单位拼接显示
		var numberSign = ""; // 数值正负号
		if (data < 0) {
			numberSign = "-";
			data = Math.abs(data); // 转为正数
		}

		if ('h' == unit) {
			//将小时转换成毫秒
			data = data * 60 * 60 * 1000;
			unit = 'ms';
		} else if ('m' == unit) {
			//将分钟转换成毫秒
			data = data * 60 * 1000;
			unit = 'ms';
		} else if ('s' == unit) {
			data = data * 1000;
			unit = 'ms';
		}

		// 如果单位是秒(s),对时间进行格式化显示为: 1h2m3s4ms
		if ('ms' == unit) {
			var ms = 1;
			var s = 1000;
			var m = 1000 * 60;
			var h = 1000 * 60 * 60;
			var d = 1000 * 60 * 60 * 24;

			// 修正数值位数
			if (data != 0) {
				data = parseFloat(data).toFixed(2); // 保留两位小数
				data = parseFloat(data); // 去除末尾的0
			}

			if (data >= d) {
				result += (data - data % d) / d + 'd'; // 取整
				data = data % d; // 求余
			}
			if (data >= h) {
				result += (data - data % h) / h + 'h'; // 取整
				data = data % h; // 求余
			}
			if (data >= m) {
				result += (data - data % m) / m + 'm';
				data = data % m;
			}
			if (data >= s) {
				result += (data - data % s) / s + 's';
				data = data % s;
			}
		}

		// 修正数值位数
		if (data != 0) {
			data = parseFloat(data).toFixed(2); // 保留两位小数
			data = parseFloat(data); // 去除末尾的0
		}

		if (!result || (result && data != 0)) {
			if ((dataType == 'CURRENCY' && (dataFormat == '¥##' || dataFormat == '$##' || dataFormat == '¥###' || dataFormat == '$'))) {
				result += unit + data;
			} else {
				if ('$' == unit || '¥' == unit) {
					result += unit + data;
				} else {
					result += data + unit;
				}
			}
		}

		return numberSign + result;
	},
	/**
	 * 根据dataType,单位,格式获取格式化函数
	 * @param type
	 * @param unit
	 * @param format
	 * @returns {fn}
	 */
	getFormatter: function (type, unit, format) {
		var checkType = this.getDataType(type);
		var fn = function (value) {
			return value;
		};
		switch (checkType) {
			case "PERCENT":
				fn = function (value) {
					return value + "%";
				};
				break;
			case "DURATION":
			case "CURRENCY":
				fn = (value) => {
					return this.formatDataViewer(value, unit, type, format);
				}
				break;
		}
		return fn;
	},
	/**
	 * 判断dataType是否是number类型
	 * @param dataType
	 * @returns {boolean}
	 */
	isNumber: function(dataType){
		return ['NUMBER', 'DOUBLE', 'FLOAT', 'LONG', 'INTEGER', 'PERCENT', 'CURRENCY', 'DURATION'].indexOf(dataType) != -1;
	},

	/**
	 * 判断dataType是否是日期类型
	 * @param dataType
	 * @returns {boolean}
	 */
	isDate: function(dataType){
		return ['DATE', 'TIMESTAMP', 'TIME', 'DATETIME'].indexOf(dataType) != -1;
	}
};
