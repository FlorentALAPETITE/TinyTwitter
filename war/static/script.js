var user;
var app = angular.module('twitt', ['ngCookies']).controller('TTController', ['$scope', '$cookies', '$cookieStore', '$window',
  function($scope, $cookies, $cookieStore, $window) {
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
    
    $scope.userId = $cookies.userId;
    $scope.username = $cookies.username;

	$scope.redirect = function(template){
		var url = document.URL;
		var host = url.substring(0,url.lastIndexOf("/"));
		var landingUrl = host + template;
		$window.location.href = landingUrl;
	}
  
    $scope.login = function(){
        gapi.client.tinyTwitterEndpoint.connectUser({
          username: $scope.slogin
        }).execute(
          function(resp){
            if(resp.id){
              $scope.redirect('/timeline.html');

              $scope.userId = $cookies.userId = resp.id;
              $scope.username = $cookies.username = resp.username;
			  $scope.$apply();
            }else{
              document.getElementById('form-error').textContent = "Cet identifiant n'existe pas."
            }
          });
    }
    
    $scope.register = function(){
      gapi.client.tinyTwitterEndpoint.insertNewUser({
         username: $scope.slogin_reg
       }).execute(
         function(resp){
            if(resp.id){
              $scope.redirect('/timeline.html');

              $scope.userId = $cookies.userId = resp.id;
              $scope.username = $cookies.username = resp.username;
			  $scope.$apply();
            }else{
              document.getElementById('form-error').textContent = "Quelque chose est incorect."
            }
         });
    }
    
    $scope.postMessage = function(){
       gapi.client.tinyTwitterEndpoint.insertNewMessage({
         message: $scope.stext,
         userId: $cookies.userId,
         username: $cookies.username
       }).execute(
         function(resp){
           $scope.listMessages();
           $scope.apply();
         });
    }
    
    $scope.listMessages = function(){
      gapi.client.tinyTwitterEndpoint.getTimeline({
        userId: $cookies.userId,
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