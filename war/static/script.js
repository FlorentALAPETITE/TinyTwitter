var user;
var app = angular.module('twitt', ['ngCookies']).controller('TTController', ['$scope', '$cookies', '$cookieStore', '$window',
  function($scope, $cookies, $cookieStore, $window) {
  	$scope.component = "timeline";

    $scope.messages = [];
    $scope.nbmessagestoadd = 5;
    $scope.slogin;
    $scope.spwd;

    $scope.following = $cookies.following;

    $scope.users = [];
    $scope.nbuserstoadd = 5;
    
    $scope.slogin_reg;
    $scope.spwd_reg;
    $scope.spwd_conf_reg;
    
    $scope.stext;
    
    $scope.userId = $cookies.userId;
    $scope.username = $cookies.username;

    $scope.execution_time_timeline;
    $scope.execution_time_post;

	$scope.redirect = function(template){
		var url = document.URL;
		var host = url.substring(0,url.lastIndexOf("/"));
		var landingUrl = host + template;
		$window.location.href = landingUrl;
	}

	$scope.setComponent = function(component) {
		$scope.component = component;
		switch(component){
			case 'timeline': $scope.listMessages(5, replace=true);
			case 'list_users': $scope.listUsers(5, replace=true);
		}
	}
  
    $scope.login = function(){
        gapi.client.tinyTwitterEndpoint.connectUser({
          username: $scope.slogin
        }).execute(
          function(resp){
            if(resp.id){
              $scope.redirect('/app.html');

              $scope.userId = $cookies.userId = resp.id;
              $scope.username = $cookies.username = resp.username;
              $scope.following = $cookies.following = resp.following;
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
              $scope.redirect('/app.html');

              $scope.userId = $cookies.userId = resp.id;
              $scope.username = $cookies.username = resp.username;
              $scope.following = resp.following;
			  $scope.$apply();
            }else{
              document.getElementById('form-error').textContent = "Quelque chose est incorrect."
            }
         });
    }

    $scope.logout = function(){
      $scope.redirect('/');

      $scope.userId = $cookies.userId = "";
      $scope.username = $cookies.username = "";
	  $scope.$apply();

    }
    
    $scope.postMessage = function(){
       var timeStart = new Date().getTime();
       gapi.client.tinyTwitterEndpoint.insertNewMessage({
         message: $scope.stext,
         userId: $cookies.userId,
         username: $cookies.username
       }).execute(
         function(resp){
           $scope.execution_time_post = (new Date().getTime()) - timeStart;
           $scope.stext = null;
           $scope.listMessages($scope.messages.length + 1, true);
           $scope.$apply();
         });
    }
    
    $scope.addFollow = function(followId){
       // var timeStart = new Date().getTime();
       gapi.client.tinyTwitterEndpoint.addFollow({
         userId: $cookies.userId,
         followId: parseInt(followId)
       }).execute(
         function(resp){
           // $scope.execution_time_post = (new Date().getTime()) - timeStart;
           $scope.following = resp.following;
           $scope.$apply();
         });
    }
    
    $scope.listMessages = function(messageLimit=5, replace=false){
      var timeStart = new Date().getTime();
      if (replace){
           $scope.messages = [];
      }
      gapi.client.tinyTwitterEndpoint.getTimeline({
        userId: $scope.userId,
        messageLimitBegin: $scope.messages.length,
        messageLimitEnd: $scope.messages.length + parseInt(messageLimit)
      }).execute(
        function(resp){
          $scope.execution_time_timeline = (new Date().getTime()) - timeStart;
		  $scope.messages = $scope.messages.concat(resp.items);
          $scope.$apply();
          if (resp.items.length < messageLimit){
          	  document.getElementById('display-more-messages').style="display:none";
          }
        });
    }
    
    $scope.listUsers = function(limit=5, replace=true){
      // var timeStart = new Date().getTime();
      if (replace){
           $scope.users = [];
      }
      gapi.client.tinyTwitterEndpoint.listUsers({
        usersLimitBegin: $scope.users.length,
        usersLimitEnd: $scope.users.length + parseInt(limit)
      }).execute(
        function(resp){
          // $scope.execution_time_timeline = (new Date().getTime()) - timeStart;
		  $scope.users = $scope.users.concat(resp.items);
          $scope.$apply();
          if (resp.items.length < limit){
          	  document.getElementById('display-more-users').style="display:none";
          }
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
			$scope.setComponent("timeline");
			}, rootApi);
		}
	}
]);