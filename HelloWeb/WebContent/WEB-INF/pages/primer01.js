(function (angular) {
	var logInCtrl = function ($scope, $resource, $http, $location) {
		var Users = $http.get('http://localhost:8080/HelloWeb/Index/checkUser')
		.success(function (data, status, headers) 
		{
			$scope.user = data;
        })
        .error(function(data, status, headers)
       {
        	console.log(data);
        	console.log(status);
        	console.log(headers);
       });
		
		$scope.loginKor = {};
		$scope.loginKor.ime = '';
		$scope.loginKor.prezime = '';
		$scope.loginKor.username = '';
		$scope.loginKor.password = '';
		
		$scope.logIn = function()
		{
			$http.post('http://localhost:8080/HelloWeb/Index/checkUser', {username: $scope.loginKor.username, password: $scope.loginKor.password})
			.success(function (user, status, headers)
			{
				if(user.vrsta === 'gradjanin')
				{
					$location.path('/main/' + user.id);
				}
				else if(user.vrsta === 'predsednik skupstine')
				{
					console.log('prolazi');
				}
			})
			.error(function (user, status, headers)
			{
				console.log(status);
				console.log(headers);
				
			});
		}
		
		$scope.register = function()
		{
			$http.post('http://localhost:8080/HelloWeb/Index/registration', {ime: $scope.loginKor.ime, prezime: $scope.loginKor.prezime,
				username: $scope.loginKor.username, password: $scope.loginKor.password})
			.success(function (user, status, headers)
			{
				$location.path('/login/');
			});
		}
		
		
		
		
	};
	
	
	var app = angular.module('app',['ui.router', 'ngResource']);
	app.controller('logInCtrl', logInCtrl);
	
	app.config(function($stateProvider, $urlRouterProvider) {

	    $urlRouterProvider.otherwise('/login');

	    $stateProvider
	    .state('login', {
	      url: '/login',
	      templateUrl: 'logIn.html',
	      controller: 'logInCtrl'
	    })
	    .state('main', {
	      url: '/main/:id',
	      templateUrl: 'main.html'
	    //  controller: 'logInCtrl'
	    })
	    .state('register', {
	      url: '/register',
	      templateUrl: 'reg-unos.html',
	      controller: 'logInCtrl'
	    })
	    
  	});
	
	
}(angular));