package org.example;

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTP;

public class ListaFicherosFTP {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("ERROR: indicar como parámetros:");
            System.out.println("servidor usuario(opcional) contraseña(opcional)");
            System.exit(1);
        }
        String servidorFTP = args[0];

        String usuario = "anonymous", password = "";
        if (args.length >= 2) {
            usuario = args[1];
        }
        if (args.length >= 3) {
            password = args[2];
        }


        FTPClient clienteFTP = new FTPClient();



        try {
            clienteFTP.connect(servidorFTP);
            int codResp = clienteFTP.getReplyCode();
            if (!FTPReply.isPositiveCompletion(codResp)) {
                System.out.printf("ERROR: Conexión rechazada con código de respuesta %d.\n", codResp);
                System.exit(2);
            }

            clienteFTP.enterLocalPassiveMode();
            clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);

            if (usuario != null && password != null) {
                boolean loginOK = clienteFTP.login(usuario, password);
                if (loginOK) {
                    System.out.printf("INFO: Login con usuario %s realizado.\n", usuario);
                }
                else {
                    System.out.printf("ERROR: Login con usuario %s rechazado.\n", usuario);
                    return;
                }
            }

            System.out.printf("INFO: Conexión establecida, mensaje de bienvenida del servidor:\n====\n%s\n====\n", clienteFTP.getReplyString());
            System.out.printf("INFO: Directorio actual en servidor: %s, contenidos:\n", clienteFTP.printWorkingDirectory());


            FTPFile[] fichServ = clienteFTP.listFiles();
            for(FTPFile f: fichServ) {
                String infoAdicFich = "";
                if(f.getType() == FTPFile.DIRECTORY_TYPE) {
                    infoAdicFich = "/";
                }
                else if(f.getType() == FTPFile.SYMBOLIC_LINK_TYPE) {
                    infoAdicFich = " -> " + f.getLink();
                }

                System.out.printf("%s%s\n", f.getName(), infoAdicFich);
            }
//--------------------------------------------Actividad4.2--------------------------------------------------------------

            clienteFTP.makeDirectory("desdeJava"); //creamos una carpeta llamada "desdeJava"
            clienteFTP.changeWorkingDirectory("desdeJava"); //entrar en la carpeta creada, llamada "desdeJava"

            //almacenar un nuevo documento "desdeElPrograma.txt" con contenido "texto"
            String contenido = "texto";
            InputStream inputStream = new ByteArrayInputStream(contenido.getBytes());
            clienteFTP.storeFile("desdeElPrograma.txt: ", inputStream);
            inputStream.close();

            //recupera el docuemtno almacenado y mostrar por pantalla su contenido
            OutputStream outputStream = new ByteArrayOutputStream();
            clienteFTP.retrieveFile("desdeElPrograma.txt", outputStream);
            System.out.println("INFO: Contenido de desdeElPrograma.txt" + outputStream.toString());
            outputStream.close();

            //recorre los ficheros y directorios en la carpeta actual
            System.out.println("INFO: Contenido de la carpeta actual:");
            FTPFile[] ficheros = clienteFTP.listFiles();
            for(FTPFile fichero: ficheros){
                System.out.println(fichero.getName());
            }

            //vuelve al directorio padre
            clienteFTP.changeToParentDirectory();

            //vuelve a recorrer los ficheros y directorios en la carpeta actual
            System.out.println("INFO: Contenidos de la carpeta actual despues de volver al directorio padre: ");
            ficheros = clienteFTP.listFiles();
            for (FTPFile fichero : ficheros){
                System.out.println(fichero.getName());
            }
//----------------------------------------------------------------------------------------------------------------------

        } catch (IOException e) {
            System.out.println("ERROR: conectando al servidor");
            e.printStackTrace();
            return;
        } finally {
            if (clienteFTP != null) {
                try {
                    clienteFTP.disconnect();
                    System.out.println("INFO: conexión cerrada.");
                } catch (IOException e) {
                    System.out.println("AVISO: no se pudo cerrar la conexión.");
                }
            }
        }
    }
}
 