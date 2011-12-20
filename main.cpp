#include <windows.h>
#include <iostream>
using namespace std;

int main(int argc, char *argv[])
{
    HKEY hKey = 0;
    char buf[2048] = {0};
    DWORD dwType = 0;
    DWORD dwBufSize = sizeof(buf);
    const char* subkey = "Applications\\PPStream.exe\\shell\\open\\command";

    if( RegOpenKey(HKEY_CLASSES_ROOT, subkey,&hKey) == ERROR_SUCCESS)
    {
        dwType = REG_SZ;
        if( RegQueryValueEx(hKey,"",0, &dwType, (BYTE*)buf, &dwBufSize) == ERROR_SUCCESS)
        {
            int counter = 0;
            for (int i = 1; i < sizeof(buf); i++) {
                buf[i-1] = buf[i];
                if (buf[i-1] == '\"') {
                   buf[i-1]='\0';
                   break;                             
                }                
                /*
                if (buf[i] == '\"') {
                      counter++;
                      if (counter >= 2) {
                          buf[i+1]='\0';
                          break;
                      }
                }
                */
            }
            cout << "PPStream location: '" << buf << "'\n";
        }
        else
        {
            cout << "PPStream not installed?\n";
        }
        RegCloseKey(hKey);

        system("TASKKILL /F /IM \"PPStream.exe\" /T");
        char adsys[256] = "\"";
        strcpy(adsys, getenv( "APPDATA" ));
        strcat(adsys, "\\PPStream\\adsys\"");
        char banner[256] = "\"";
        strcpy(banner, getenv( "APPDATA" ));
        strcat(banner, "\\PPStream\\banner\"");
        cout << "Deleting " << adsys << " ...\n";
        char rm[256] = "rm -r ";
        strcat(rm, adsys);
        system(rm);
        cout << "Deleting " << banner << " ...\n";
        rm[6] = '\0';
        strcat(rm, banner);
        system(rm);
        
        STARTUPINFO si;
        PROCESS_INFORMATION pi;
        ZeroMemory( &si, sizeof(si));
        si.cb = sizeof(si);
        ZeroMemory( &pi, sizeof(pi));
        char params[1024] = " >> ";
        strcat(params, adsys);
        strcat(params, " 2>> ");
        strcat(params, banner);
        cout << "Starting " << buf << " ...\n";
        CreateProcess( buf, params, NULL, NULL, FALSE, 0,NULL, NULL, &si, &pi );
        Sleep(3000);
    }
    else
    {
        cout << "Can not open key\n";
        Sleep(10000);
    }
    

    return EXIT_SUCCESS;
}
