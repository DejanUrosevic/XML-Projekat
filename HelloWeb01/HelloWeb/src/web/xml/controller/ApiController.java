package web.xml.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api") 
public class ApiController {
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public RoleUsername login(
			final HttpServletRequest req) throws ServletException, IOException {
		
		final HttpServletRequest request = (HttpServletRequest) req;
		
		final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7); // The part after "Bearer "
   
	    
		Claims claims = Jwts.parser().setSigningKey("asdf").parseClaimsJws(token).getBody();
		
		String rola = (String) claims.get("roles");
		return new RoleUsername(rola, claims.getSubject());
	}
	
	/*
	 //Pokusaj redirekcije, ali isto radi kao i klasa gore, ali ovo je jedino sto sam nasao da je obecavalo da ce raditi
	  public ResponseEntity<Object> login(
			final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException, URISyntaxException {
		
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		
		final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7); // The part after "Bearer "
   
	    try {
	    	Claims claims = Jwts.parser().setSigningKey("asdf").parseClaimsJws(token).getBody();
			
			//System.out.println(claims.getId());
			
			String rola = (String) claims.get("roles");
			//return new RoleUsername(rola, claims.getSubject());
			return new ResponseEntity<Object>(new RoleUsername(rola, claims.getSubject()), HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("uhvatio sam ga");
			
		}
	    
	    URI uri = new URI("http://localhost:8080/HelloWeb/pages/index.html#/login");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(uri);
	    return new ResponseEntity<Object>(httpHeaders, HttpStatus.SEE_OTHER);
		
	}
	 */
	
	@SuppressWarnings("unused")
	   private static class RoleUsername {
	       public String role;
	       public String username;

	      public RoleUsername(final String role, final String username)
	      {
	    	  this.role = role;
	    	  this.username = username;
	      }
	   }
}
