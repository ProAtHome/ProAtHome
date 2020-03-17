package com.proathome.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FotoPerfil extends HttpServlet{
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){

        try{
            
           ServletFileUpload servletFile = new ServletFileUpload(new DiskFileItemFactory());
           
            List<FileItem> multifiles = servletFile.parseRequest(request);
            
            for (FileItem item: multifiles) {
                
                item.write(new File("C:/xampp/htdocs/ProAtHome/assets/img/fotoPerfil/" + item.getName()));
                
            }
            
            System.out.println("Cargado...");
            
            response.sendRedirect("http://localhost/ProAtHome/perfil/estudiante");
            
        }catch(ServletException ex){
            
            System.out.println(ex.getMessage());
            
        }catch(IOException | FileUploadException ex){
            
            System.out.println(ex.getMessage());
            
        }catch(Exception ex){
            
            System.out.println(ex.getMessage());
            
        }
        
    }//Fin método doPost.
    
}
