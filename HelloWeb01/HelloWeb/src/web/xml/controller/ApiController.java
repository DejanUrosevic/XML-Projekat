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
	public RoleUsername login(final HttpServletRequest req) throws ServletException, IOException {

		final HttpServletRequest request = (HttpServletRequest) req;

		/**
		 * Provera da li je korisnik ulogovan(ima token), ako nije dovidjenja,
		 * nema pristupa.
		 */
		final String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.equals("Bearer null") || authHeader.endsWith("null")) {
			throw new ServletException("Missing or invalid Authorization header.");
		}

		String token = authHeader.substring(7); // The part after "Bearer "

		Claims claims = Jwts.parser().setSigningKey("asdf").parseClaimsJws(token).getBody();

		String rola = (String) claims.get("roles");
		return new RoleUsername(rola, claims.getSubject());
	}

	@SuppressWarnings("unused")
	private static class RoleUsername {
		public String role;
		public String username;

		public RoleUsername(final String role, final String username) {
			this.role = role;
			this.username = username;
		}
	}
}
