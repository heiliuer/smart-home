#include<reg52.h>               

//函数声明 
void SendStr(unsigned char *s);
void DelayUs2x(unsigned char t);
void DelayMs(unsigned char t);
void sendIOData();

unsigned int times = 0;

unsigned char send[5];

sbit KEY1 = P0 ^ 0;  //定义按键输入端口
sbit KEY2 = P0 ^ 1;  //定义按键输入端口
sbit LED1 = P1 ^ 0;
sbit LED2 = P1 ^ 1;
int flag1 = 1;
int flag2 = 1;

/*------------------------------------------------
 串口初始化
 ------------------------------------------------*/
void InitUART(void) {
	SCON = 0x50;		        // SCON: 模式 1, 8-bit UART, 使能接收  
	TMOD |= 0x20;               // TMOD: timer 1, mode 2, 8-bit 重装
	TH1 = 0xFD;               // TH1:  重装值 9600 波特率 晶振 11.0592MHz  
	TR1 = 1; //启动定时器                
	EA = 1;                  //打开总中断
	ES = 1;                  //打开串口中断
}
void SendByte(unsigned char dat);
/*------------------------------------------------
 主函数
 ------------------------------------------------*/

void main(void) {
	int i = 100;
	InitUART();

	//先输出0，再输出1，才能真正输出
	P0 = 0x00;
	P1 = 0x00;
	P2 = 0x00;
	P3 = 0x00;

	P0 = 0xff;
	P1 = 0xff;
	P2 = 0xff;
	P3 = 0xff;

	KEY1 = 1; //按键输入端口电平置高
	KEY2 = 1;

	while (1) {
//		SendByte(0xff);
//		while(i-->0){
//			j=100;
//			while(j-->0);
//		}

		if (!KEY1 || !KEY2)  //如果检测到低电平，说明按键按下
				{
			DelayMs(10); //延时去抖，一般10-20ms
			if (!KEY1 || !KEY2)     //再次确认按键是否按下，没有按下则退出
					{
				flag1 = !KEY1;
				flag2 = !KEY2;
				while (!KEY1 || !KEY2)
					;     //如果确认按下按键等待按键释放，没有释放则一直等待

				if (flag1) {
					LED1 = !LED1;
				}
				if (flag2) {
					LED2 = !LED2;
				}
				sendIOData();
				
			}
		}
		
		//主循环中添加其他需要一直工作的程序

	}
}

/*------------------------------------------------
 发送一个字节
 ------------------------------------------------*/
void SendByte(unsigned char dat) {
	SBUF = dat;
	while (!TI)
		;
	TI = 0;
}
/*------------------------------------------------
 发送一个字符串
 ------------------------------------------------*/
void SendStr(unsigned char *s) {
	while (*s != '\n')     // \0 表示字符串结束标志，通过检测是否字符串末尾
	{
		SendByte(*s);
		s++;
	}
}

void receive_wait() {
	while (!RI)
		;
	RI = 0;
}

void sendIOData() {
	send[0] = P0;
	send[1] = P1;
	send[2] = P2;
	send[3] = P3;
	send[4] = '\n';
	SendStr(send);
}

/*------------------------------------------------
 串口数据 控制组件
 ------------------------------------------------*/
void UART_SER()
interrupt 4
{
	if(RI)                        //判断是接收中断产生
	{
		RI=0;                      //标志位清零
		switch(SBUF) {
			case 0xaa:                      //读取所有IO口的电平状态
			receive_wait();
			sendIOData();
			break;
			case 0xa0:						//设置P0的8位电平
			receive_wait();
			P0=SBUF;
			sendIOData();
			break;
			case 0xa1:						//设置P1的8位电平
			receive_wait();
			P1=SBUF;
			sendIOData();
			break;
			case 0xa2:						//设置P2的8位电平
			receive_wait();
			P2=SBUF;
			sendIOData();
			break;
			case 0xa3:						//设置P3的8位电平
			receive_wait();
			P3=P3&0x03|SBUF;
			sendIOData();
			break;
		}
	}
//	if(TI)                        //如果是发送标志位，清零
//	{
//		TI=0;
//	}
}

/*------------------------------------------------
 uS延时函数，含有输入参数 unsigned char t，无返回值
 unsigned char 是定义无符号字符变量，其值的范围是
 0~255 这里使用晶振12M，精确延时请使用汇编,大致延时
 长度如下 T=tx2+5 uS 
 ------------------------------------------------*/
void DelayUs2x(unsigned char t) {
	while (--t)
		;
}
/*------------------------------------------------
 mS延时函数，含有输入参数 unsigned char t，无返回值
 unsigned char 是定义无符号字符变量，其值的范围是
 0~255 这里使用晶振12M，精确延时请使用汇编
 ------------------------------------------------*/
void DelayMs(unsigned char t) {

	while (t--) {
		//大致延时1mS
		DelayUs2x(245);
		DelayUs2x(245);
	}
}

