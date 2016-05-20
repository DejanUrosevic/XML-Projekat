(function (angular) {
	
	/**
	 * Ovo je kontroler za logovanje i registraciju korisnika sistema
	 */
	var logInCtrl = function (mainService, $scope, $resource, $http, $location, $state) 
	{
		$scope.token = null;
        $scope.error = null;
        $scope.gradj = false;
        $scope.odbor = false;
        $scope.predSkup = false;
        $scope.userName = '';
        $scope.passWord = '';
        $scope.rolaUser = null;
        
        //ovde ces nakon sto povuces podatke iz geta login-a dobiti token
        //medjutim, ako username i pass ne postoje, token se ne stvori i onda pukne u klasi ApiController
        //zato sto mu saljes token koji nije validan, pa pukne u klasi.
        //TO DO -- Odradi validaciju, da ako nema korisnika, da ne pukne to sranje, da ga nekako vrati samo na login i sve ispocetka.
        //Sredjen TO-DO na angularu, ali nije jos na back-end-u
        $scope.login = function() {
            $scope.error = null;
            mainService.login($scope.userName, $scope.passWord).then(function(token) {
                $scope.token = token;
                $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                if(localStorage)
        		{
                	localStorage.setItem('key', token);	
        		}
                
                $scope.checkRoles();
                
            },
            function(error){
                $scope.error = error
                $scope.userName = '';
            });
                    
        }
        
        
        $scope.checkRoles = function() 
        {
        	$http.get('http://localhost:8080/HelloWeb/api/role').then(function(response)
        	{      
        		
        		$scope.rolaUser = response.data;
        		if($scope.rolaUser.role === 'odbornik' || $scope.rolaUser.role === 'gradjanin' || $scope.rolaUser.role === 'predsednik skupstine')
        		{
        			$state.go('main');
        		}
        		else
        		{
        			$state.go('login');
        		}
        	});
        }
        
        $scope.logout = function() {
            $scope.userName = '';
            $scope.token = null;
            $http.defaults.headers.common.Authorization = '';
            localStorage.clear();
            $state.go('login');
        }

        $scope.loggedIn = function() {
            return $scope.token !== null;
        }
        
        
        //----------------------------------------------------------------GET
		
		/*
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
	   */	
		//-------------------------------------------------------------------------REGISTRACIJA
		
		$scope.loginKor = {};
		$scope.loginKor.ime = '';
		$scope.loginKor.prezime = '';
		$scope.loginKor.username = '';
		$scope.loginKor.password = '';
		
		/**
		 * Metoda za registraciju korisnika na sistem
		 */
		$scope.register = function()
		{
			$http.post('http://localhost:8080/HelloWeb/Index/registration', {ime: $scope.loginKor.ime, prezime: $scope.loginKor.prezime,
				username: $scope.loginKor.username, password: $scope.loginKor.password})
			.success(function (user, status, headers)
			{
				$location.path('/login');
			});
		}
		
	}
	
	
	/**
	 * Kontroler za glavnu stranicu sistema
	 */
	var mainPageCtrl = function ($scope, $resource, $http, $location, $stateParams, $state) 
	{
		//JAKO JAKO BITNO DA SE svaki put prilikom slanja request-a posalje i token
		//mora opet da se postavlja Authorization header!!!
		console.log(localStorage.getItem('key') + "+++");
		$http.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('key');
		
		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda treba prebaciti odmah na login posto nema token kod sebe
		 */
		if(localStorage.getItem('key') !== null){ 
			$http.get('http://localhost:8080/HelloWeb/api/role').then(function(response)
		    {      
				$scope.rolaUser = response.data;       
		    });
		}else{
			$state.go('login');
		}   
		
		//ovako ne radi, iznad je zastita na front-end-u, ali koristim da bi testirao back-end
		/*$http.get('http://localhost:8080/HelloWeb/api/role').then(function(response)
		{      
			$scope.rolaUser = response.data;       
		})*/
		
	}
	
	
	var app = angular.module('app',['ui.router', 'ngResource']);
	app.controller('logInCtrl', logInCtrl);
	app.controller('mainPageCtrl', mainPageCtrl);
	
	app.config(function($stateProvider, $urlRouterProvider) {

	    $urlRouterProvider.otherwise('/login');

	    $stateProvider
	    .state('login', {
	      url: '/login',
	      templateUrl: 'logIn.html',
	      controller: 'logInCtrl'
	    })
	    .state('main', {
	      url: '/main',
	      templateUrl: 'main.html',
	      controller: 'mainPageCtrl'
	    })
	    .state('register', {
	      url: '/register',
	      templateUrl: 'reg-unos.html',
	      controller: 'logInCtrl'
	    })
	    .state('greska', {
	      url: '/greska',
	      templateUrl: 'greska.html'	  
	    })
	    
  	});
	
	app.service('mainService', function($http, $state) { 
	    return {
	        login : function(email, lozinka) {
	            return $http.post('http://localhost:8080/HelloWeb/Index/checkUser', {username: email, password: lozinka}).then(function(response) {
	            	
	            	return response.data.token;
	            	
	            });
	        },

	        hasRole : function() {
	            return $http.get('http://localhost:8080/HelloWeb/api/role').then(function(response){
	                console.log(response);
	                return response.data;
	            });
	        }
	    };
	});
	
	
	
}(angular));