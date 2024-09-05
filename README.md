引用
implementation 'io.github.wyy-dev:xypush:0.0.5'

华为
1. 主应用放入[agconnect-services.json](app%2Fagconnect-services.json)
2. 修改参数 
PushConstants.huaweiAPPID = "";
PushConstants.huaweiBadgeClassName = "className;

VIVO
1. 主应用 -> build.gradle 配置
   manifestPlaceholders = [
   "VIVO_APPKEY": "",
   "VIVO_APPID" : "",
   "HONOR_APPID": ""
   ]
2. 修改参数
PushConstants.vivoBadgeClassName = "className;

OPPO
1. 修改参数
PushConstants.oppoAppKey = "";
PushConstants.oppoAppSecret = "";

小米
1. 修改参数
PushConstants.xiaoMiAppID = "2882303761520197947";
PushConstants.xiaoMiAppKey = "5742019729947";


