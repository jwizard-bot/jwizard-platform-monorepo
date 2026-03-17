package xyz.jwizard.jwl.transport.http.resolver;

import org.eclipse.jetty.server.Request;
import xyz.jwizard.jwl.transport.http.route.MatchResult;

import java.lang.reflect.Parameter;

public interface ArgumentResolver {
    boolean supports(Parameter parameter);

    Object resolve(Parameter parameter, Request req, MatchResult match) throws Exception;
}
