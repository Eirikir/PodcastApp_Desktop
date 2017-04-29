;This file will be executed next to the application bundle image
;I.e. current directory will contain folder PodRunner with application files
[Setup]
AppId={{podcast_application}}
AppName=PodRunner
AppVersion=1.0
AppVerName=PodRunner 1.0
AppPublisher=ERST0704
AppComments=PodRunner
AppCopyright=Copyright (C) 2017
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName=C:\Program Files\PodRunner
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=ERST0704
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=PodRunner-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=PodRunner\PodRunner.ico
UninstallDisplayIcon={app}\PodRunner.ico
UninstallDisplayName=PodRunner
WizardImageStretch=No
WizardSmallImageFile=PodRunner-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Dirs]
Name: "{app}";
Name: "{app}\app\Podcasts"; Permissions: users-full

[Files]
Source: "PodRunner\PodRunner.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "PodRunner\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\PodRunner"; Filename: "{app}\PodRunner.exe"; IconFilename: "{app}\PodRunner.ico"; Check: returnTrue()
Name: "{commondesktop}\PodRunner"; Filename: "{app}\PodRunner.exe";  IconFilename: "{app}\PodRunner.ico"; Check: returnFalse()


[Run]
Filename: "{app}\PodRunner.exe"; Description: "{cm:LaunchProgram,PodRunner}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\PodRunner.exe"; Parameters: "-install -svcName ""PodRunner"" -svcDesc ""PodRunner"" -mainExe ""PodRunner.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\PodRunner.exe "; Parameters: "-uninstall -svcName PodRunner -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
