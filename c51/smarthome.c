#include<reg52.h>               



void SendStr(unsigned char *s);

unsigned int times=0;

unsigned char send[5];

/*------------------------------------------------
                    串口初始化
------------------------------------------------*/
void InitUART  (void)
{
    SCON  = 0x50;		        // SCON: 模式 1, 8-bit UART, 使能接收  
    TMOD |= 0x20;               // TMOD: timer 1, mode 2, 8-bit 重装
    TH1   = 0xFD;               // TH1:  重装值 9600 波特率 晶振 11.0592MHz  
	TR1=1; //启动定时器                
    EA    = 1;                  //打开总中断
    ES    = 1;                  //打开串口中断
}
void SendByte(unsigned char dat);                            
/*------------------------------------------------
                    主函数
------------------------------------------------*/
void main (void)
{
	int i=100,j;
	InitUART();
	//先输出0，再输出1，才能真正输出
    P0=0x00;
	P1=0x00;
	P2=0x00;
	P3=0x00;

	P0=0xff;
	P1=0xff;
	P2=0xff;
	P3=0xff;
	while (1){
		SendByte(0xff);
		while(i-->0){
			j=100;
			while(j-->0);
		}
	}
}

/*------------------------------------------------
                    发送一个字节
------------------------------------------------*/
void SendByte(unsigned char dat)
{
	 SBUF = dat;
	 while(!TI);
	 TI = 0;
}
/*------------------------------------------------
                    发送一个字符串
------------------------------------------------*/
void SendStr(unsigned char *s)
{
 while(*s!='\n')// \0 表示字符串结束标志，通过检测是否字符串末尾
  {
	  SendByte(*s);
	  s++;
  }
}


void receive_wait()
{
    while(!RI);
    RI = 0;
}

void sendIOData(){
 	 send[0]=P0;
	 send[1]=P1;
	 send[2]=P2;
	 send[3]=P3;
	 send[4]='\n';
	 SendStr(send);
}

/*------------------------------------------------
                    串口数据 控制组件
------------------------------------------------*/
void UART_SER() interrupt 4 
{
   if(RI)                        //判断是接收中断产生
     {
		  RI=0;                      //标志位清零
		  switch(SBUF){
		  	case 0xaa://读取所有IO口的电平状态
				receive_wait();
				sendIOData();
				break;
		  	case 0xa0://设置P0的8位电平
				receive_wait();
				P0=SBUF;
				sendIOData();
				break;
		  	case 0xa1://设置P1的8位电平
				receive_wait();
				P1=SBUF;
				sendIOData();
				break;
		  	case 0xa2://设置P2的8位电平
				receive_wait();
				P2=SBUF;
				sendIOData();
				break;
		  	case 0xa3://设置P3的8位电平
				receive_wait();
				P3=P3&0x03|SBUF;
				sendIOData();
				break;
		  }
	 }
    if(TI)                        //如果是发送标志位，清零
	{
     	TI=0;
	}
} 



