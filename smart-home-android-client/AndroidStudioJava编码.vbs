on error resume next   
Set WshShell=WScript.CreateObject("Shell.Application")   
dirPath=WshShell.BrowseForFolder(0, "��ѡ��·��", 0, "").items().item().path   
if right(dirPath,1)<>"\" then dirPath=dirpath&"\"   
ma=inputbox("������Ҫת��Ϊ�ı���","","UTF-8")   
if ma="" or dirPath="\" or msgbox("��ʹ��ǰ��ȷ���ѱ����ļ���"&dirPath,1)=2 then WScript.Quit   
  
'�����ļ����µ��ļ�   
Set FSO = CreateObject("scripting.filesystemobject")   
Set f = FSO.GetFolder(dirPath)   
Set fs = f.files   
For Each fileN in fs   
FN=dirPath&fileN.name&""   
if ".java"=lcase(right(FN,5)) then Call WriteToFile2(FN, ReadFile(FN, "ANSI_X3.4-1986"), ma)   
Next   
Set FSO = Nothing   
wscript.echo "ȫ���ɹ�"   
  
'����ļ��ı���   
Function CheckCode (FileUrl)   
Dim slz   
set slz = CreateObject("Adodb.Stream")   
slz.Type = 1   
slz.Mode = 3   
slz.Open   
slz.Position = 0   
slz.Loadfromfile FileUrl   
Bin=slz.read(2)   
if AscB(MidB(Bin,1,1))=&HEF and AscB(MidB(Bin,2,1))=&HBB Then   
Codes="UTF-8"   
elseif AscB(MidB(Bin,1,1))=&HFF and AscB(MidB(Bin,2,1))=&HFE Then   
Codes="Unicode"   
else   
Codes="ANSI"   
end if   
slz.Close   
set slz = Nothing   
'wscript.echo Codes  
CheckCode=Codes   
End Function   
  
'��ָ���ı����ȡ�ļ�   
Function ReadFile(FileUrl, CharSet)   
On Error Resume Next   
Dim Str   
Set stm = CreateObject("Adodb.Stream")   
stm.Type = 2   
stm.mode = 3   
stm.charset = CharSet   
stm.Open   
stm.loadfromfile FileUrl   
Str = stm.readtext   
stm.Close   
Set stm = Nothing   
'wscript.echo Str   
ReadFile = Str   
End Function   
  
'��ָ���ı���д�ļ�   
Function WriteToFile (FileUrl, Str, CharSet)   
On Error Resume Next   
Set stm = CreateObject("Adodb.Stream")   
stm.Type = 2   
stm.mode = 3   
stm.charset = CharSet   
stm.Open   
stm.WriteText Str  
stm.SaveToFile FileUrl, 2   
stm.flush   
stm.Close   
Set stm = Nothing   
End Function  
  
Function WriteToFile2 (FileUrl, Str, CharSet)   
On Error Resume Next   
Set stm = CreateObject("Adodb.Stream")   
stm.Type = 2   
stm.mode = 3   
stm.charset = CharSet   
stm.Open   
stm.WriteText Str  
stm.Position = 3  
Set newStream = CreateObject("Adodb.Stream")   
newStream.mode = 3  
newStream.Type = 1  
newStream.Open()  
stm.CopyTo(newStream)  
newStream.SaveToFile FileUrl,2  
stm.flush   
stm.Close   
Set stm = Nothing   
newStream.flush   
newStream.Close   
Set newStream = Nothing   
End Function  