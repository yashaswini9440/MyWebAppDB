package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class homeController {
	
	@Autowired
	UserRepo userrepo;
	
	@RequestMapping("/login")
	public String  loginUser(@RequestBody User user) {
		
		String respose="";
		
		User u=null;
		try {
			 u=userrepo.findById(user.getUsername()).get();
				String a=u.getPassword();
				String b=user.getPassword();
				
			 if(a.equals(b)) {
				 respose= "success";
				}else {
				 respose= "loginfail";
				}
		}
		catch(Exception e){
			respose= "NewUser";
			
		}
		
		
		 return respose;
	}
	
	@PostMapping("/getUserDetails")
	public User getUserDetails(@RequestBody User user) {
		
		
		return userrepo.findById(user.getUsername()).get();
			
	}
	
	
	@PostMapping("/RegisterUser")
	public String addUser(@RequestBody User user) {
		String res="";
		
		try {
		userrepo.save(user);
		res="Sucess";
		}catch(Exception e) {
			res="Error";		}
		return 		res;	
	}
	
	@RequestMapping(value="/uploadFile", method=RequestMethod.POST)
	public int uploadFile(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
		
		User user=userrepo.findById(name).get();
		
		int wc=0;
		String err="";
		
		File convertFile = new File("/var/tmp/"+file.getOriginalFilename());
		String filepath="/var/tmp/"+file.getOriginalFilename();
		String res=" ";
		
	      try {
	    	  convertFile.createNewFile();
			  FileOutputStream fout = new FileOutputStream(convertFile);
			  fout.write(file.getBytes());
			  fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			File f1=new File("/var/tmp/"+file.getOriginalFilename()); 
			  String[] words=null; 
			       
			  FileReader fr = new FileReader(f1); 
			  BufferedReader br = new BufferedReader(fr);
			  String s;
			  while((s=br.readLine())!=null) 
			  {
			     words=s.split(" ");
			     wc=wc+words.length; 
			  }
			  fr.close();
			res="success";
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res="fail";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res="fail";
		}
		user.setWc(wc);
		user.setFilepath(filepath);
		userrepo.save(user);
		
		return wc;
	}
	
	@RequestMapping(value="/fileDownload", method=RequestMethod.GET)
	public ResponseEntity<Object> fileDownload(@RequestParam("username") String username) throws IOException{
		
		User user=userrepo.findById(username).get();
		String filename = user.getFilepath();
	      File file = new File(filename);
	      

			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			  HttpHeaders headers = new HttpHeaders();
			  
			  headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
			  headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			  headers.add("Pragma", "no-cache");
			  headers.add("Expires", "0");
			  
			  ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(
			     file.length()).contentType(MediaType.parseMediaType("application/txt")).body(resource);
			  return responseEntity;
		
	   }
	
}
