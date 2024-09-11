#include <stdio.h>
#include <unistd.h>
#include "ohos_init.h"
#include "cmsis_os2.h"
#include "hi_wifi_api.h"
#include "lwip/ip_addr.h"
#include "lwip/netifapi.h"
#include "lwip/sockets.h"

#include <stdint.h>
#include <string.h>

#include "wifiiot_gpio.h"
#include "wifiiot_gpio_ex.h"
#include "wifiiot_pwm.h"
#include "wifiiot_adc.h"
#include "wifiiot_i2c.h"
#include "wifiiot_errno.h"
#include "oled_ssd1306.h"
#include "aht20.h"
#include "hi_pwm.h"

//static char request[] = "Hello";
static char response[128] = "";
/**************************************************
 * @brief 
 * 扩展练习，在检测中加入蜂鸣器,当温度高于40度，或者环境异味浓度达到800时，蜂鸣器报警
 * 环境监测，获取环境的温湿度值，显示在LED屏幕上,并通过网络传输给javaSocket服务器
 *
 ***************************************************/

#define AHT20_BAUDRATE 400*1000 //定义波特率宏
#define AHT20_I2C_IDX WIFI_IOT_I2C_IDX_0 //定义湿度信道
//设置通道名称为通道5，ADC信道
#define GAS_SENSOR_CHAN_NAME WIFI_IOT_ADC_CHANNEL_5
#define ADC_RESOLUTION 2048

//声明全局变量，获取返回值，温度值，湿度值，以及要显示的字符串信息
uint32_t reval=0;//返回值
float shiduval=0.0f;//湿度值
float wenduval=0.0f;//温度值
unsigned short data=0;//空气异味感应器
char line[32]={0};//初始化字符串信息

float shidutmp=0.0f;//存储湿度值的临时变量
float wendutmp=0.0f;//存储温度值的临时变量
short qiweitmp=0;//存储异味值的临时变量
unsigned short guangqiangtmp=0;//空气异味感应器

int Limitwendu;
int Limitshidu;
int Limitqiwei;
int Limitguangqiang;
int button_flag;
char strshidu[20]={0},strwendu[20]={0},strqiwei[20]={0},strguangqiang[20]={0};//定义转换为字符串的变量
char linstr[80]={0};//定义拼接字符串的最终值
/**************传感器收据收集****************************************************************************************/
/**
 * @brief 
 * 初始化设备的方法
 * 
 */
void init(void){
    GpioInit();//初始化管脚
    OledInit();//初始化LED屏幕
    OledFillScreen(0);//填充屏幕
    I2cInit(AHT20_I2C_IDX,AHT20_BAUDRATE);//初始化烟雾感应器

    //蜂鸣器引脚设置为pwm功能
    IoSetFunc(WIFI_IOT_IO_NAME_GPIO_9,WIFI_IOT_IO_FUNC_GPIO_9_PWM0_OUT);
    PwmInit(WIFI_IOT_PWM_PORT_PWM0);//初始化引脚

    //初始化温湿度板卡
    while(WIFI_IOT_SUCCESS!=AHT20_Calibrate()){
        printf("AHT20 SENSOR init fail!!!\r\n");
        usleep(2000);//过2000微秒再试    
    }
    
    // 初始化 三值阈值
    Limitwendu = 40;
    Limitshidu = 20;
    Limitqiwei = 1000;
    Limitguangqiang = 600;
}

void show_rightnow(void){
    button_flag = 1;
    printf("run here\r\n");
}

char temprep[100] = {0};

char f1[] = "xianshi"; //: oled立刻显示
char f2[] = "yvzhiwendu"; //: 设置新的温度报警阈值
char f3[] = "yvzhishidu"; //: 设置新的温度报警阈值
char f4[] = "yvzhiqiwei"; //: 设置新的温度报警阈值
char f5[] = "wendu"; //: 降温
char f6[] = "shidu"; //: 洒水增加湿度
char f7[] = "qiwei"; //: 开风扇换气
char f8[] = "yvzhiguangqiang"; // 设置新的光强报警阈值
char f9[] = "zheguang"; // 遮光
char f10[] = "buguang"; // 补光

char tempwendu[10],tempshidu[10],tempqiwei[10],tempguangqiang[10];
char num[4] = {0};
void connect_net_task(const char *host, unsigned short port)
{
    init();//调用初始化的方法

    while (1)
    { 

        sleep(2);//每隔2秒刷新一次
        /*********************网络连接**************************************/
            /* code */
        ssize_t retval = 0;
        int sockfd = socket(AF_INET, SOCK_STREAM, 0); // TCP socket

        struct sockaddr_in serverAddr = {0};
        serverAddr.sin_family = AF_INET;   // AF_INET表示IPv4协议
        serverAddr.sin_port = htons(port); // 端口号，从主机字节序转为网络字节序
        if (inet_pton(AF_INET, host, &serverAddr.sin_addr) <= 0)
        { // 将主机IP地址从“点分十进制”字符串 转化为 标准格式（32位整数）
            printf("inet_pton failed!\r\n");
            goto Udo_cleanup;
        }

        // 尝试和目标主机建立连接，连接成功会返回0 ，失败返回 -1
        if (connect(sockfd, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0)
        {
            printf("button connect failed!\r\n");
            goto Udo_cleanup;
        }
        printf("button connect to server %s success!\r\n", host);


        // 建立连接成功之后，这个TCP socket描述符 —— sockfd 就具有了 “连接状态”，发送、接收 对端都是 connect 参数指定的目标主机和端口
        memset(temprep, 0, sizeof(temprep));
        retval = recv(sockfd, temprep, sizeof(temprep), 0);
        // retval = recv(sockfd, &response, sizeof(response), 0);
        if (retval <= 0)
        {
            printf("button send response from server failed or done, %ld!\r\n", retval);
            goto Udo_cleanup;
        }
        temprep[retval] = '\0';
        printf("button {%s} %ld from server done!\r\n", temprep, retval);
        //retval = send(sockfd, request, sizeof(request), 0);
        char sed[] = "end";
        retval = send(sockfd, sed, sizeof(sed), 0);
        if (retval < 0)
        {
            printf("button send request failed!\r\n");
            goto Udo_cleanup;
        }
        printf("button send request{%s} %ld to server done!\r\n", linstr, retval);
        
        
        if(strlen(temprep) >= 9){
            memset(num, 0, sizeof(num));
            unsigned int i, top = 0;
            int valu;
            if(temprep[5] == 'w'){
                OledFillScreen(0);//填充屏幕
                for(i = 10; i < strlen(temprep); i ++)
                    num[top ++] = temprep[i];
                valu = atoi(num);
                Limitwendu = valu;
                OledShowString(0, 0, "tmp set successfully!", 1);
            }else if(temprep[5] == 's'){
                OledFillScreen(0);//填充屏幕
                for(i = 10; i < strlen(temprep); i ++)
                    num[top ++] = temprep[i];
                valu = atoi(num);
                Limitshidu = valu;
                OledShowString(0, 0, "hum set successfully!", 1);
            }else if(temprep[5] == 'q'){
                OledFillScreen(0);//填充屏幕
                for(i = 10; i < strlen(temprep); i ++)
                    num[top ++] = temprep[i];
                valu = atoi(num);
                Limitqiwei = valu;
                OledShowString(0, 0, "gas set successfully!", 1);
            }else{
                OledFillScreen(0);//填充屏幕
                for(i = 15; i < strlen(temprep); i ++)
                    num[top ++] = temprep[i];
                valu = atoi(num);
                Limitguangqiang = valu;
                OledShowString(0, 0, "sun set successfully!", 1);
            }
        }
        else {
            if(strcmp(temprep, f1) == 0){ // 立刻显示
                button_flag = 1;
            }else if(strcmp(temprep, f5) == 0){ // 降温
                OledFillScreen(0);//填充屏幕
                char tempout[] = "reduce temperature";
                OledShowString(0, 0, tempout, 1);
            }else if(strcmp(temprep, f6) == 0){ // 洒水
                OledFillScreen(0);//填充屏幕
                char tempout[] = "purling water";
                OledShowString(0, 0, tempout, 1);
            }else if(strcmp(temprep, f7) == 0){ // 换气
                OledFillScreen(0);//填充屏幕
                char tempout[] = "open fans";
                OledShowString(0, 0, tempout, 1);
            }else if(strcmp(temprep, f9) == 0){ // 遮光
                OledFillScreen(0);//填充屏幕
                char tempout[] = "shading";
                OledShowString(0, 0, tempout, 1);
            }else if(strcmp(temprep, f10) == 0){ // 补光
                OledFillScreen(0);//填充屏幕
                char tempout[] = "Fill-in light";
                OledShowString(0, 0, tempout, 1);
            }
        }

        
    Udo_cleanup:
        printf("button do_cleanup...\r\n");
        closesocket(sockfd);
    }
}

/****************************************网络传输******************************************************/
void conent_tcp_server(const char *host, unsigned short port)
{
    init();//调用初始化的方法

    while (1)
    { 

        /*******************传感器数据接收****************************************/    
        if(AHT20_StartMeasure()!=WIFI_IOT_SUCCESS){//调用测量的方法，测量失败，输出失败的消息
            printf("AHT20 CELIANG fail!!\r\n");

        }
        if(AHT20_GetMeasureResult(&wenduval,&shiduval)!=WIFI_IOT_SUCCESS){//接收温度和湿度值，判断是否接受成功
            printf("jieshou fail!!!");
        }
        //使用adc函数，读取烟雾传感器数据给data赋值
        AdcRead(GAS_SENSOR_CHAN_NAME,&data,WIFI_IOT_ADC_EQU_MODEL_4,WIFI_IOT_ADC_CUR_BAIS_DEFAULT,0);  
        
        /****接收传感器传递的数值准备传递给服务器********/
        wendutmp=wenduval;
        // 把浮点数wendutmp转换为字符串，存放在strwendu中。
        sprintf(strwendu,"%.2f",wendutmp);
        
        shidutmp=shiduval;
         // 把浮点数shidutmp转换为字符串，存放在strshidu中。
        sprintf(strshidu,"%.2f",shidutmp);
        
        qiweitmp=data;
        // 把浮点数shidutmp转换为字符串，存放在strshidu中。
        sprintf(strqiwei,"%d",qiweitmp);

        guangqiangtmp = (int)(1.0 * (qiweitmp + wendutmp + shidutmp) / 1.6);
        sprintf(strguangqiang, "%d", guangqiangtmp);
        /*******************************/
        

        /***********将转换的字符串拼接*******************/
        sprintf(linstr, "%s,%s,%s,%s", strwendu,strshidu,strqiwei,strguangqiang);//接受到的传感器数据转为字符串准备上传到服务器进行处理
        printf("linstr: %s\r\n",linstr);//输出获取的传感器数据
        /************************************************/


        if(wenduval >= Limitwendu || data >= Limitqiwei || shiduval <= Limitshidu || guangqiangtmp >= Limitguangqiang){//当烟雾和异味浓度超过800，则开始蜂鸣器报警，或者当温度值大于40度时开始报警
            printf("daya=%d\r\n",data);
             //蜂鸣器的声音
            uint16_t freqDivisor=34052;//占空比，频率
            PwmStart(WIFI_IOT_PWM_PORT_PWM0,freqDivisor/2,freqDivisor);//启动蜂鸣器,设置频率
            usleep(2000000);//设置间隔时间
            PwmStop(WIFI_IOT_PWM_PORT_PWM0);
            usleep(1000000);//设置间隔时间
        }else{
            PwmStop(WIFI_IOT_PWM_PORT_PWM0);//停止报警
        }        
        if(button_flag == 1){
            OledFillScreen(0);//填充屏幕
            sprintf(tempwendu,"%s%s", "tmp: ", strwendu);
            sprintf(tempshidu,"%s%s", "hum: ", strshidu);
            sprintf(tempqiwei,"%s%s", "gas: ", strqiwei);
            sprintf(tempguangqiang,"%s%s", "sun: ", strguangqiang);
            OledShowString(0, 0, "Sensor values:", 1);                    // 在0列0行输出提示信息
            OledShowString(0, 1, tempwendu, 1);                           // 0列1行在液晶屏上显示温度值
            OledShowString(0, 2, tempshidu, 1);                           // 0列2行在液晶屏上显示湿度值
            OledShowString(0, 3, tempqiwei, 1);                           // 在0列3行液晶屏上显示气味值
            OledShowString(0, 3, tempguangqiang, 1);                           // 在0列4行液晶屏上显示光强值
            button_flag = 0;
        }
        sleep(2);//每隔2秒刷新一次

        /*********************网络连接**************************************/
            /* code */
        ssize_t retval = 0;
        int sockfd = socket(AF_INET, SOCK_STREAM, 0); // TCP socket

        struct sockaddr_in serverAddr = {0};
        serverAddr.sin_family = AF_INET;   // AF_INET表示IPv4协议
        serverAddr.sin_port = htons(port); // 端口号，从主机字节序转为网络字节序
        if (inet_pton(AF_INET, host, &serverAddr.sin_addr) <= 0)
        { // 将主机IP地址从“点分十进制”字符串 转化为 标准格式（32位整数）
            printf("inet_pton failed!\r\n");
            goto do_cleanup;
        }

        // 尝试和目标主机建立连接，连接成功会返回0 ，失败返回 -1
        if (connect(sockfd, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0)
        {
            printf("connect failed!\r\n");
            goto do_cleanup;
        }
        printf("connect to server %s success!\r\n", host);

        // 建立连接成功之后，这个TCP socket描述符 —— sockfd 就具有了 “连接状态”，发送、接收 对端都是 connect 参数指定的目标主机和端口
        //retval = send(sockfd, request, sizeof(request), 0);
        retval = send(sockfd, linstr, sizeof(linstr), 0);
        if (retval < 0)
        {
            printf("send request failed!\r\n");
            goto do_cleanup;
        }
        printf("send done!\r\n");

        retval = recv(sockfd, response, sizeof(response), 0);
        // retval = recv(sockfd, &response, sizeof(response), 0);
        if (retval <= 0)
        {
            printf("send response from server failed or done, %ld!\r\n", retval);
            goto do_cleanup;
        }
        response[retval] = '\0';
        if(response[0] != 'b'){
            OledFillScreen(0);//填充屏幕
            OledShowString(0, 0, "Some indicators are abnormal. Check the web page now.", 1);  // 在0列0行输出提示信息
        }
        printf("recv {%s}\r\n", response);

    do_cleanup:
        printf("do_cleanup...\r\n");
        closesocket(sockfd);
    }
}





