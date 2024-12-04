package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.stream.Collectors;

public class ServidorNFS {

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");

        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        while (true) {
            try {
                String comando = dis.readUTF();
                System.out.println("Comando recebido: " + comando);

                String[] partes = comando.split(" ", 2);
                String operacao = partes[0];
                String argumento = partes.length > 1 ? partes[1] : null;

                switch (operacao) {
                    case "readdir":
                        dos.writeUTF(readdir(argumento));
                        break;
                    case "rename":
                        String[] argsRename = argumento.split(" ", 2);
                        dos.writeUTF(rename(argsRename[0], argsRename[1]));
                        break;
                    case "create":
                        dos.writeUTF(create(argumento));
                        break;
                    case "remove":
                        dos.writeUTF(remove(argumento));
                        break;
                    default:
                        dos.writeUTF("Comando inválido");
                }
            } catch (Exception e) {
                dos.writeUTF("Erro: " + e.getMessage());
            }
        }
    }

    private static String readdir(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (Files.isDirectory(path)) {
                return Files.list(path)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.joining(", "));
            } else {
                return "Erro: Caminho não é um diretório";
            }
        } catch (IOException e) {
            return "Erro ao listar o diretório: " + e.getMessage();
        }
    }

    private static String rename(String oldName, String newName) {
        try {
            Files.move(Paths.get(oldName), Paths.get(newName));
            return "Arquivo renomeado com sucesso";
        } catch (IOException e) {
            return "Erro ao renomear o arquivo: " + e.getMessage();
        }
    }

    private static String create(String filePath) {
        try {
            Files.createFile(Paths.get(filePath));
            return "Arquivo criado com sucesso";
        } catch (IOException e) {
            return "Erro ao criar o arquivo: " + e.getMessage();
        }
    }

    private static String remove(String filePath) {
        try {
            Files.delete(Paths.get(filePath));
            return "Arquivo removido com sucesso";
        } catch (IOException e) {
            return "Erro ao remover o arquivo: " + e.getMessage();
        }
    }
}
