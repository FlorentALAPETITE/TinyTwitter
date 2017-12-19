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

		$scope.setCookie = function(key, value){
			document.cookie = key + "=" + value;
		}

		$scope.getCookie = function(key){
		    var name = key + "=";
		    var ca = document.cookie.split(';');
		    for(var i = 0; i < ca.length; i++) {
		        var c = ca[i];
		        while (c.charAt(0) == ' ') {
		            c = c.substring(1);
		        }
		        if (c.indexOf(name) == 0) {
		            return c.substring(name.length, c.length);
		        }
		    }
		    return "";
		}

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
						$scope.setCookie("userid", resp.id);
						alert($scope.getCookie("userid"));
						$scope.redirect("/timeline.html");
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
					$scope.setCookie("userid", resp.id);
					alert($scope.getCookie("userid"));
					$scope.redirect("/timeline.html");
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