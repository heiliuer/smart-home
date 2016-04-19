on error resume next   
Set WshShell=WScript.CreateObject("Shell.Application")   
dirPath=WshShell.BrowseForFolder(0, "请选择路径", 0, "").items().item().path   
if right(dirPath,1)<>"\" then dirPath=dirpath&"\"   
ma=inputbox("请输入要转换为的编码","","UTF-8")   
if ma="" or dirPath="\" or msgbox("在使用前请确认已备份文件夹"&dirPath,1)=2 then WScript.Quit   
  
'遍历文件夹下的文件   
Set FSO = CreateObject("scripting.filesystemobject")   
Set f = FSO.GetFolder(dirPath)   
Set fs = f.files   
For Each fileN in fs   
FN=dirPath&fileN.name&""   
if ".java"=lcase(right(FN,5)) then Call WriteToFile2(FN, ReadFile(FN, "ANSI_X3.4-1986"), ma)   
Next   
Set FSO = Nothing   
wscript.echo "全部成功"   
  
'检测文件的编码   
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
  
'以指定的编码读取文件   
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
  
'以指定的编码写文件   
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