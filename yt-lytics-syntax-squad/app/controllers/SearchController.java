package controllers;
import models.*;

import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;

import java.util.*;

import views.html.*;
import play.i18n.Messages;

import play.i18n.MessagesApi;
import javax.inject.Inject;

public class SearchController  extends Controller{

    private final FormFactory formFactory;
    private final MessagesApi messagesApi;

    public List<SearchResults> displayResults = new ArrayList<>();

    @Inject
    public SearchController(FormFactory formFactory, MessagesApi messagesApi) {
        this.formFactory = formFactory;
        this.messagesApi = messagesApi;
    }

    public Result search(Http.Request request){
        Form<Search> searchForm = formFactory.form(Search.class).bindFromRequest(request);
        Messages messages = messagesApi.preferred(request);

        if(searchForm.hasErrors()){
            return badRequest();
        }
        Search data = searchForm.get();  // Access the form data
        String searchKey = data.getKey();
        if(searchKey != null && !searchKey.isEmpty()) {
            List<YouTubeVideo> YTVideosList = new ArrayList<>();
            try {
                YTVideosList = YouTubeSearch.Search(searchKey);
            } catch (Exception e) {
                System.out.println("check exception==== " + e);
            }

            System.out.println("comes here to check111 " + searchForm + data + searchKey);
            SearchResults sr = new SearchResults(searchKey, YTVideosList);
            displayResults.add(0, sr);
        }

        return ok(search.render(searchForm,displayResults,messages));
    }
}