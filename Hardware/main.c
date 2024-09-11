#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "ohos_init.h"
#include "cmsis_os2.h"
#include "wifi_device.h"
#include "lwip/netifapi.h"
#include "lwip/api_shell.h"
#include "wifi_utils.h"
#include "tcp_client.h"
#include "wifiiot_gpio.h"
#include "wifiiot_gpio_ex.h"
#include "hi_gpio.h"
#include "wifiiot_i2c.h"
#include "wifiiot_adc.h"
#include "wifiiot_errno.h"
#include "aht20.h"
#include "oled_ssd1306.h"

#define task_num 1
#define host "192.168.143.151"

unsigned short task_port[task_num] = {8001};

static void NetWorkTask(void *arg)
{
    (void)arg;
    connect_wifi(); // 连接AP
    printf("begin demo\n");
    unsigned short port = 5001;
    conent_tcp_server(host, port);
}

static void net_task(void *arg)
{
    int param = *((int *)arg);
    connect_net_task(host, task_port[param]);
}


// 创建按键处理函数OnButtonPressed
static void OnButtonPressed(char *arg)
{
    (void)arg;
    printf("Key pressed\r\n");
    show_rightnow();

}

static void StartS3Task(void)
{

    GpioInit();
    IoSetFunc(WIFI_IOT_IO_NAME_GPIO_5, WIFI_IOT_IO_FUNC_GPIO_5_GPIO); // button
    GpioSetDir(WIFI_IOT_IO_NAME_GPIO_5, WIFI_IOT_GPIO_DIR_IN);
    IoSetPull(WIFI_IOT_IO_NAME_GPIO_5, WIFI_IOT_IO_PULL_UP);

    GpioRegisterIsrFunc(WIFI_IOT_IO_NAME_GPIO_5, WIFI_IOT_INT_TYPE_EDGE, WIFI_IOT_GPIO_EDGE_FALL_LEVEL_LOW,
                        OnButtonPressed, NULL); // 注册按键事件方法
    while (1)
    {
        sleep(1);
    }
}

static void NetWorkDemo(void)
{
    osThreadAttr_t attr;

    attr.name = "NetWorkTask";
    attr.attr_bits = 0U;
    attr.cb_mem = NULL;
    attr.cb_size = 0U;
    attr.stack_mem = NULL;
    attr.stack_size = 10240;
    attr.priority = osPriorityNormal;

    if (osThreadNew(NetWorkTask, NULL, &attr) == NULL)
    {
        printf("[NetWorkDemo] Falied to create WifiConnectTask!\n");
    }
    for (int i = 0; i < task_num; i++)
    {
        attr.name = "net_task";
        int temp = i;
        if (osThreadNew(net_task, (void *)&temp, &attr) == NULL)
        {
            printf("[NetWorkDemo] Falied to create WifiConnectTask!\n");
        }
    }

    attr.name = "StartS3Task";
    attr.attr_bits = 0U;
    attr.cb_mem = NULL;
    attr.cb_size = 0U;
    attr.stack_mem = NULL;
    attr.stack_size = 1024;
    attr.priority = osPriorityNormal;

    if (osThreadNew((osThreadFunc_t)StartS3Task, NULL, &attr) == NULL)
    {
        printf("[StartS3Task] Falied to create StartS3Task!\n");
    }
    printf("\r\n[StartS3Task] Succ to create StartS3Task!\n");
}

APP_FEATURE_INIT(NetWorkDemo);
