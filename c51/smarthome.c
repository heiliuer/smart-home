#include<reg52.h>               



void SendStr(unsigned char *s);

unsigned int times=0;

unsigned char send[5];

/*------------------------------------------------
                    ���ڳ�ʼ��
------------------------------------------------*/
void InitUART  (void)
{
    SCON  = 0x50;		        // SCON: ģʽ 1, 8-bit UART, ʹ�ܽ���  
    TMOD |= 0x20;               // TMOD: timer 1, mode 2, 8-bit ��װ
    TH1   = 0xFD;               // TH1:  ��װֵ 9600 ������ ���� 11.0592MHz  
	TR1=1; //������ʱ��                
    EA    = 1;                  //�����ж�
    ES    = 1;                  //�򿪴����ж�
}
void SendByte(unsigned char dat);                            
/*------------------------------------------------
                    ������
------------------------------------------------*/
void main (void)
{
	int i=100,j;
	InitUART();
	//�����0�������1�������������
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
                    ����һ���ֽ�
------------------------------------------------*/
void SendByte(unsigned char dat)
{
	 SBUF = dat;
	 while(!TI);
	 TI = 0;
}
/*------------------------------------------------
                    ����һ���ַ���
------------------------------------------------*/
void SendStr(unsigned char *s)
{
 while(*s!='\n')// \0 ��ʾ�ַ���������־��ͨ������Ƿ��ַ���ĩβ
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
                    �������� �������
------------------------------------------------*/
void UART_SER() interrupt 4 
{
   if(RI)                        //�ж��ǽ����жϲ���
     {
		  RI=0;                      //��־λ����
		  switch(SBUF){
		  	case 0xaa://��ȡ����IO�ڵĵ�ƽ״̬
				receive_wait();
				sendIOData();
				break;
		  	case 0xa0://����P0��8λ��ƽ
				receive_wait();
				P0=SBUF;
				sendIOData();
				break;
		  	case 0xa1://����P1��8λ��ƽ
				receive_wait();
				P1=SBUF;
				sendIOData();
				break;
		  	case 0xa2://����P2��8λ��ƽ
				receive_wait();
				P2=SBUF;
				sendIOData();
				break;
		  	case 0xa3://����P3��8λ��ƽ
				receive_wait();
				P3=P3&0x03|SBUF;
				sendIOData();
				break;
		  }
	 }
    if(TI)                        //����Ƿ��ͱ�־λ������
	{
     	TI=0;
	}
} 



