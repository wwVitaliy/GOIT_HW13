import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

/**
 * Напишіть програму, що буде взаємодіяти з API https://jsonplaceholder.typicode.com.
 * <p>
 * Можна використовувати стандартні можливості Java (клас HttpURLConnection), або використати сторонні рішення на зразок Apache Fluent API, Apache HTTPClient, Jsoup.
 * <p>
 * Завдання 1
 * Програма повинна містити методи для реалізації наступного функціоналу:
 * <p>
 * створення нового об'єкта в https://jsonplaceholder.typicode.com/users. Можливо, ви не побачите одразу змін на сайті. Метод працює правильно, якщо у відповідь на JSON з об'єктом повернувся такий самий JSON, але зі значенням id більшим на 1, ніж найбільший id на сайті.
 * <p>
 * оновлення об'єкту в https://jsonplaceholder.typicode.com/users. Можливо, ви не побачите одразу змін на сайті. Вважаємо, що метод працює правильно, якщо у відповідь ви отримаєте оновлений JSON (він повинен бути таким самим, що ви відправили).
 * <p>
 * видалення об'єкта з https://jsonplaceholder.typicode.com/users. Тут будемо вважати коректним результат - статус відповіді з групи 2xx (наприклад, 200).
 * <p>
 * отримання інформації про всіх користувачів https://jsonplaceholder.typicode.com/users
 * <p>
 * отримання інформації про користувача за id https://jsonplaceholder.typicode.com/users/{id}
 * <p>
 * отримання інформації про користувача за username - https://jsonplaceholder.typicode.com/users?username={username}
 * <p>
 * Завдання 2
 * Доповніть програму методом, що буде виводити всі коментарі до останнього поста певного користувача і записувати їх у файл.
 * <p>
 * https://jsonplaceholder.typicode.com/users/1/posts Останнім вважаємо пост з найбільшим id.
 * <p>
 * https://jsonplaceholder.typicode.com/posts/10/comments
 * <p>
 * Файл повинен називатись user-X-post-Y-comments.json, де Х - id користувача, Y - номер посту.
 * <p>
 * Завдання 3
 * Доповніть програму методом, що буде виводити всі відкриті задачі для користувача з ідентифікатором X.
 * <p>
 * https://jsonplaceholder.typicode.com/users/1/todos.
 * <p>
 * Відкритими вважаються всі задачі, у яких completed = false.
 */

public class Homework13 {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String JSON_HOLDER = "https://jsonplaceholder.typicode.com";
    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        //-----------------Task #1

        // Create new user
        User myUser = createDefaultUser();

        // Send POST request to JSON_HOLDER
        printResponse(
                postNewUser(JSON_HOLDER, myUser));

        // Send PUT request to JSON_HOLDER
        myUser.setName("C. Bauch");
        printResponse(
                putUser(JSON_HOLDER, myUser));

        // Send DELETE request to JSON_HOLDER
        printResponse(
                deleteUser(JSON_HOLDER, myUser));

        // Send GET request to JSON_HOLDER
        printResponse(
                getAllUsers(JSON_HOLDER));

        // Send GET request to JSON_HOLDER to get user by ID
        printResponse(
                getUserById(JSON_HOLDER,5));

        // Send GET request to JSON_HOLDER to get user by username
        printResponse(
                getUserByUsername(JSON_HOLDER,"Maxime_Nienow"));

        //-----------------Task #2

        // Get all comments from the last user's post, print them to console and write to a file
        writeLastCommentsToAFile(JSON_HOLDER, 3);

        //-----------------Task #3

        // Get all open user's tasks and print them to console
        printAllOpenTasks(JSON_HOLDER, 3);

    }

    /**
     * Prints all user's tasks that are open (not compleated)
     *
     * @param host   Host address
     * @param userId Id of the user
     * @throws IOException
     * @throws InterruptedException
     */
    private static void printAllOpenTasks(String host, int userId) throws IOException, InterruptedException {
        ArrayList<Task> taskList = getListOfAllTasks(host, userId);

        for (Task t : taskList) {
            if (!t.isCompleted()) {
                System.out.println(t.getTitle() + "\n");
            }
        }
    }

    /**
     * Gets a list of all user's tasks
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return the list of all user's tasks
     * @throws IOException
     * @throws InterruptedException
     */
    private static ArrayList<Task> getListOfAllTasks(String host, int userId) throws IOException, InterruptedException {
        return GSON.fromJson(
                getAllUsersTasks(host, userId).body(),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
    }

    /**
     * Sends GET request to receive all user's tasks from the host
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getAllUsersTasks(String host, int userId) throws IOException, InterruptedException {
        return getAll(host + "/users/" + userId + "/todos");
    }

    /**
     * Writes comments from last user's post to a file.
     * File's name user-X-post-Y-comments.json, where X - user's ID, Y - post ID
     *
     * @param host   Host address
     * @param userId Id of the user
     * @throws IOException
     * @throws InterruptedException
     */
    private static void writeLastCommentsToAFile(String host, int userId) throws IOException, InterruptedException {
        ArrayList<Comment> listOfLastComments = getListOfLastComments(host, userId);

        for (Comment comment : listOfLastComments) {
            System.out.println(comment.getId() + "   ---   " + comment.getName());
            System.out.println(comment.getBody() + "\n");
        }

        String fileName = "user-" + userId + "-post-" + getLastPostId(host, userId) + "-comments.json";
        File file = new File(fileName);
        file.createNewFile();

        try (FileWriter writer = new FileWriter(file)) {
            for (Comment comment : listOfLastComments) {
                writer.write(comment.getId() + "   ---   " + comment.getName() + "\n");
                writer.write(comment.getBody() + "\n\n");
            }
        }
    }

    /**
     * Sends GET request to receive all comments to a post
     *
     * @param host   Host address
     * @param postId Id of the post to get all comments for
     * @return Response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getAllCommentsForPost(String host, int postId) throws IOException, InterruptedException {
        return getAll(host + "/posts/" + postId + "/comments");

    }

    /**
     * Gets a list of all user's posts
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return A list of all user's posts
     * @throws IOException
     * @throws InterruptedException
     */
    private static ArrayList<Post> getListOfPosts(String host, int userId) throws IOException, InterruptedException {
        return GSON.fromJson(
                getAllUsersPosts(host, userId).body(),
                new TypeToken<ArrayList<Post>>() {
                }.getType());
    }

    /**
     * Gets a list of all comments to user's last post
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return the list of all comments to user's last post
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static ArrayList<Comment> getListOfLastComments(String host, int userId) throws IOException, InterruptedException {
        return GSON.fromJson(
                getAllCommentsForPost(
                        host,
                        getLastPostId(host, userId)).body(),
                new TypeToken<ArrayList<Comment>>() {
                }.getType());
    }

    /**
     * Gets the ID of the last users post in the host
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return The ID of the last users post in the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static int getLastPostId(String host, int userId) throws IOException, InterruptedException {
        ArrayList<Post> postList = getListOfPosts(host, userId);
        return postList.get(postList.size() - 1).getId();
    }

    /**
     * Sends GET request to receive all user's posts from the host
     *
     * @param host   Host address
     * @param userId Id of the user
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getAllUsersPosts(String host, int userId) throws IOException, InterruptedException {
        return getAll(host + "/users/" + userId + "/posts");
    }

    /**
     * Sends GET request to receive user data with defined username from host
     *
     * @param host     Host address
     * @param username Username of the user to get data about
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getUserByUsername(String host, String username) throws IOException, InterruptedException {
        return getAll(host + "/users?username=" + username);
    }

    /**
     * Sends GET request to receive user data with defined ID from host
     *
     * @param host Host address
     * @param id   Id of the user to get data about
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getUserById(String host, int id) throws IOException, InterruptedException {
        return getAll(host + "/users/" + id);
    }

    /**
     * Sends GET request to receive all users data from host
     *
     * @param host Host address
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getAllUsers(String host) throws IOException, InterruptedException {
        return getAll(host + "/users");
    }

    /**
     * Sends GET request to receive data from uri
     *
     * @param uri Address of th e resource
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> getAll(String uri) throws IOException, InterruptedException {
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-type", "application/json; charset=UTF-8")
                .GET()
                .build();
        return CLIENT.send(requestGet, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Sends a DELETE request to delete user
     *
     * @param host Host address
     * @param user User to delete
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> deleteUser(String host, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);

        HttpRequest requestDelete = HttpRequest.newBuilder()
                .uri(URI.create(host + "/posts/1"))
                .header("Content-type", "application/json; charset=UTF-8")
                .DELETE()
                .build();

        return CLIENT.send(requestDelete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Sends a PUT request to update user
     *
     * @param host Host address
     * @param user User to update
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> putUser(String host, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);

        HttpRequest requestPut = HttpRequest.newBuilder()
                .uri(URI.create(host + "/posts/1"))
                .header("Content-type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return CLIENT.send(requestPut, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Sends a POST request to create a new user
     *
     * @param host Host address
     * @param user User to post
     * @return A response from the host
     * @throws IOException
     * @throws InterruptedException
     */
    private static HttpResponse<String> postNewUser(String host, User user) throws IOException, InterruptedException {
        String requestBody = GSON.toJson(user);

        HttpRequest requestPost = HttpRequest.newBuilder()
                .uri(URI.create(host + "/users"))
                .header("Content-type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return CLIENT.send(requestPost, HttpResponse.BodyHandlers.ofString());


    }

    /**
     * Prints info about response from request
     *
     * @param response response from request
     */
    private static void printResponse(HttpResponse<String> response) {
        System.out.println("\n" + response.toString());
        System.out.println("\n" + "response.body() = \n" + response.body());
        System.out.println("\n===============================================================");
    }

    /**
     * Creates user with defined parameters
     *
     * @return Created user
     */
    private static User createDefaultUser() {
        User user = new User();
        user.setId(3);
        user.setName("Clementine Bauch");
        user.setEmail("Nathan@yesenia.net");
        user.setAddress(new Address("Douglas Extension",
                "Suite 847",
                "McKenziehaven",
                "59590-4157",
                new Geo("-68.6102",
                        "-47.0653")));
        user.setPhone("1-463-123-4447");
        user.setWebsite("ramiro.info");
        user.setCompany(new Company(
                "Romaguera-Jacobson",
                "Face to face bifurcated interface",
                "e-enable strategic applications"));
        return user;
    }

}
