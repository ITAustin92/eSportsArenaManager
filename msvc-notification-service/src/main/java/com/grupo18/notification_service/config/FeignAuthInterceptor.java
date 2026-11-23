package com.grupo18.notification_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// Propaga el JWT del usuario a las llamadas Feign entre microservicios.
// Sin esto, el servicio llamado no recibe la cabecera Authorization y responde 401,
// porque cada microservicio valida el token por su cuenta (segunda barrera).
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
		if (atributos instanceof ServletRequestAttributes servlet) {
			String autorizacion = servlet.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
			if (
				autorizacion != null &&
				!autorizacion.isBlank() &&
				!template.headers().containsKey(HttpHeaders.AUTHORIZATION)
			) {
				template.header(HttpHeaders.AUTHORIZATION, autorizacion);
			}
		}
	}
}
