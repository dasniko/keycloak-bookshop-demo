package dasniko.keycloak.shop;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global exception mapper for user-friendly error pages
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

	@Inject
	Template error;

	@Override
	public Response toResponse(Exception exception) {
		String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred";
		String exceptionType = exception.getClass().getSimpleName();
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		// Get stack trace as string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String stackTrace = sw.toString();

		TemplateInstance errorPage = error
			.data("errorMessage", errorMessage)
			.data("exceptionType", exceptionType)
			.data("timestamp", timestamp)
			.data("stackTrace", stackTrace);

		return Response
			.status(Response.Status.INTERNAL_SERVER_ERROR)
			.type(MediaType.TEXT_HTML)
			.entity(errorPage.render())
			.build();
	}
}
