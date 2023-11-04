package sandipchitale.hspfrtrcwc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@SpringBootApplication
public class HspfrtrcwcApplication {

	// backed by WebClient
	record Post(long id, long userid, String title, boolean completed) {}

	@Bean
	@Qualifier("post-web-client")
	WebClient postWebClient(WebClient.Builder builder) {
		return builder.baseUrl("https://jsonplaceholder.typicode.com/posts").build();
	}

	interface PostService {
		@GetExchange
		List<Post> getPosts();

		@GetExchange("/{id}")
		Post getPost(@PathVariable("id") long id);
	}

	@Bean
	PostService postClient(@Qualifier("post-web-client") WebClient webClient) {
		HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
		return proxyFactory.createClient(PostService.class);
	}


	@Bean
	CommandLineRunner clrPostService(PostService postService) {
		return (String... args) -> {
			System.out.println("Posts using PostClient backed by WebClient: " + postService.getPosts());
			System.out.println("Post using PostClient backed by WebClient: " + postService.getPost(1));
		};
	}

	// backed by RestClient
	record Todo(long id, String title, boolean completed, int userId) {}

	@Bean
	@Qualifier("todo-rest-client")
	RestClient todoRestClient(RestClient.Builder builder) {
		return builder.baseUrl("https://jsonplaceholder.typicode.com/todos").build();
	}

	interface TodoService {
		@GetExchange
		List<Todo> getTodos();

		@GetExchange("/{id}")
		Todo getTodo(@PathVariable("id") long id);
	}

	@Bean
	TodoService todoService(@Qualifier("todo-rest-client") RestClient restClient) {
		HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return proxyFactory.createClient(TodoService.class);
	}

	@Bean
	CommandLineRunner clrTodoService(TodoService todoService) {
		return (String... args) -> {
			System.out.println("Todos using TodoClient backed by RestClient: " + todoService.getTodos());
			System.out.println("Todo using TodoClient backed by RestClient: " + todoService.getTodo(1));
		};
	}

	// backed by RestTemplate
	record Comment(long id, String name) {}

	@Bean
	@Qualifier("comment-rest-template")
	RestTemplate commentRestTemplate(RestTemplateBuilder builder) {
		// It appears that rootUri() does not work.
		return builder
				// It appears that rootUri() does not work.
				// You have to use @HttpExchange on the interface.
				// .rootUri("https://jsonplaceholder.typicode.com/comments")
				.build();
	}

	@HttpExchange("https://jsonplaceholder.typicode.com/comments")
	interface CommentService {
		@GetExchange
		List<Comment> getComments();

		@GetExchange("/{id}")
		Comment getComment(@PathVariable("id") long id);
	}

	@Bean
	CommentService commentService(@Qualifier("comment-rest-template") RestTemplate restTemplate) {
		HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(RestTemplateAdapter.create(restTemplate)).build();
		return proxyFactory.createClient(CommentService.class);
	}

	@Bean
	CommandLineRunner clrCommentService(CommentService commentService) {
		return (String... args) -> {
			System.out.println("Comments using CommentClient backed by RestTemplate: " + commentService.getComments());
			System.out.println("Comment using CommentClient backed by RestTemplate: " + commentService.getComment(1));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(HspfrtrcwcApplication.class, args);
	}

}
