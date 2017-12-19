var user;
var app = angular.module('twitt', ['ngCookies']).controller('TTController', ['$cookieStore','$scope', '$window',
  function($cookieStore, $scope, $window) {
    $scope.messages = [{
      id: '0',
      author: "Billy",
      text: "Hello, my name is Billy"
    }, {
      id: '1',
      author: "Pedro",
      text: "Hola, me llamo Pedro"
    }];
    $scope.slogin;
    $scope.spwd;
    
    $scope.slogin_reg;
    $scope.spwd_reg;
    $scope.spwd_conf_reg;
    
    $scope.stext;
    
    
    console.log("HERE 2 : "+$cookieStore.get('userId'));
    $scope.userId = $cookieStore.get('userId');
  
    $scope.login = function(){
        gapi.client.tinyTwitterEndpoint.connectUser({
          username: $scope.slogin
        }).execute(
          function(resp){
            if(resp.id){
              var userId = resp.id; //FAIRE UNE SESSION
              $cookieStore.put('userId',resp.id);
              $scope.userId = $cookieStore.get('userId');
              console.log("HERE 1 : "+$cookieStore.get('userId'));
              var url = document.URL;
              var host = url.substring(0,url.lastIndexOf("/"));
              var landingUrl = host + "/timeline.html";
              $window.location.href = landingUrl;
            }else{
              alert("Incorrect username");
            }
          });
    }
    
    $scope.register = function(){
      gapi.client.tinyTwitterEndpoint.insertNewUser({
         username: $scope.slogin_reg
       }).execute(
         function(resp){
           //FAIRE UNE SESSION OU REDIRIGER VERS LOGIN (Mieux)
          var url = document.URL;
          var host = url.substring(0,url.lastIndexOf("/"));
          var landingUrl = host + "/timeline.html";
          $window.location.href = landingUrl;
         });
    }
    
    $scope.postMessage = function(){
       gapi.client.tinyTwitterEndpoint.insertNewMessage({
         id: "Auto increment ??",
         author: $scope.sname,
         text: $scope.stext
       }).execute(
         function(resp){
           $scope.listMessages();
           $scope.apply();
         });
    }
    
    $scope.listMessages = function(){
      gapi.client.tinyTwitterEndpoint.getTimeline({
        userId: "0",
        messageLimit: "5"
      }).execute(
        function(resp){
          $scope.messages = resp.items;
          $scope.$apply();
        });
    }

    // little hack to be sure that apis.google.com/js/client.js is loaded
    // before calling
    // onload -> init() -> window.init() -> then here
    $window.init = function() {
      console.log("windowinit called");
      var rootApi = 'https://tinytwitter-189016.appspot.com/_ah/api';
      gapi.client.load('tinyTwitterEndpoint', 'v1', function() {
        console.log("twitt api loaded");
        $scope.listMessages();
      }, rootApi);
    }
  }
]);