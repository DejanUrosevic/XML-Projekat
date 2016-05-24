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
            	if(token === undefined)
            	{
            		$state.go('login');
            	}
            	else
            	{
            		$scope.token = token;
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    if(localStorage)
              		{
                      	localStorage.setItem('key', token);	
              		}
                      
                    $scope.checkRoles();
            	}
            	
              
                
            },
            function(error){
                $scope.error = error
                $scope.userName = '';
            });
                    
        }
        
        /**
         * Ovde se proveravaju role i nakon provere se rediretkuje na /main stranicu.
         */
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
	 * Kontroler za glavnu stranicu sistema, ovde lepimo na $scope klijenta koji se ulogovao.
	 */
	var mainPageCtrl = function ($scope, $resource, $http, $location, $stateParams, $state, $sce) 
	{
		//JAKO JAKO BITNO DA SE svaki put prilikom slanja request-a posalje i token
		//mora opet da se postavlja Authorization header!!!
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
		
		
		$scope.sviAkti = function()
		{
			$state.go('pregledAkata');
		}
		
		$scope.addAkt = function()
		{
			$state.go('dodPropis');
			
		}
		
	}
	
	var sviAktiCtrl = function ($scope, $resource, $http, $location, $stateParams, $state) 
	{
		//JAKO JAKO BITNO DA SE svaki put prilikom slanja request-a posalje i token
		//mora opet da se postavlja Authorization header!!!
		$http.defaults.headers.common.Authorization = 'Bearer ' + localStorage.getItem('key');
		
		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda treba prebaciti odmah na login posto nema token kod sebe
		 */
		if(localStorage.getItem('key') !== null){ 
			$http.get('http://localhost:8080/HelloWeb/clan/all').then(function(response)
		    {      
				$scope.akti = response.data.deo.glava[0].odeljak[0].clan[0];       
		    });
		}else{
			$state.go('login');
		}
		
		$scope.pregledAkta = function(aktId) 
		{
			$location.path('/sviAkti/akt/' + aktId);
		}
	}
	
	var addPropisCtrl = function($scope, $resource, $http, $location, $stateParams, $state)
	{
		//predefinsane vrednosti, jer pravi problem na backend-u.
		$scope.stavBroj = '';
		$scope.stavTekst = '';
		$scope.clanTekst = '';
		$scope.glavaNaziv = '';
		
		
		/**
		 * Ovde bi faktikci trebalo da se posalje na bazu gotov propis,
		 * ali u trenutku pisanja ovog koda, mi nemamo ni | slova B ob Baze.
		 * Stoga samo kreira novi xml na hard disku.
		 */
		$scope.zavrsiPropis = function()
		{	
			$http.post('http://localhost:8080/HelloWeb/clan/noviPropis', {nazivPropisa: $scope.propis.naziv, nazivDeo: $scope.deo.naziv,
				nazivGlave: $scope.glavaNaziv, nazivClana: $scope.clan.naziv, opisClana: $scope.clan.opis, redniBrojStava: $scope.stavBroj,
				stavTekst: $scope.stavTekst, tekstClana: $scope.clanTekst})
			.then(function(response)
			{
				$state.go('opcije');
			});
			
		}
		
		/**
		 * Mozemo dodati jos jedan novi deo.
		 */
		$scope.noviDeo = function()
		{
			$http.post('http://localhost:8080/HelloWeb/clan/noviDeo', {nazivDeo: $scope.deo.naziv,
				nazivGlave: $scope.glavaNaziv, nazivClana: $scope.clan.naziv, opisClana: $scope.clan.opis, redniBrojStava: $scope.stavBroj,
				stavTekst: $scope.stavTekst, tekstClana: $scope.clanTekst})
			.then(function(response)
			{
				$state.go('opcije');
			});
			
		}
		
		/**
		 * Mozemo dodati jos jednu novu glavu.
		 */
		$scope.novaGlava = function()
		{
			$http.post('http://localhost:8080/HelloWeb/clan/novaGlava', {nazivGlave: $scope.glavaNaziv, nazivClana: $scope.clan.naziv, 
				opisClana: $scope.clan.opis, redniBrojStava: $scope.stavBroj,
				stavTekst: $scope.stavTekst, tekstClana: $scope.clanTekst})
			.then(function(response)
			{
				$state.go('opcije');
			});
		}
		
		$scope.noviClan = function(){
			$http.post('http://localhost:8080/HelloWeb/clan/noviClan', {nazivClana: $scope.clan.naziv, 
				opisClana: $scope.clan.opis, redniBrojStava: $scope.stavBroj,
				stavTekst: $scope.stavTekst, tekstClana: $scope.clanTekst})
			.then(function(response)
			{
				$state.go('opcije');
			});
		}
		
		$scope.noviStav = function(){
			$http.post('http://localhost:8080/HelloWeb/clan/noviStav', {redniBrojStava: $scope.stavBroj,
				stavTekst: $scope.stavTekst})
			.then(function(response)
			{
				$state.go('opcije');
			});
		}

	}
	
	
	var app = angular.module('app',['ui.router', 'ngResource']);
	app.controller('logInCtrl', logInCtrl);
	app.controller('mainPageCtrl', mainPageCtrl);
	app.controller('sviAktiCtrl', sviAktiCtrl);
	app.controller('addPropisCtrl', addPropisCtrl);
	
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
	    .state('pregledAkata', {
	      url: '/sviAkti',
	      templateUrl: 'sviAkti.html',
	      controller: 'sviAktiCtrl'
	    })
	    .state('izabranAkt', {
	      url: '/sviAkti/akt/:id',
	      templateUrl: 'pregled-akta.html',
	      controller: 'sviAktiCtrl'
	    })
	    .state('dodPropis', {
	      url: '/noviPropis',
	      templateUrl: 'dodaj-propis.html',
	      controller: 'addPropisCtrl'
	    })
	    .state('opcije', {
	      url: '/opcije',
	      templateUrl: 'opcije.html',
	      controller: 'addPropisCtrl'
	    })
	    .state('noviDeo', {
	      url: '/noviDeo',
	      templateUrl: 'dodaj-deo.html',
	      controller: 'addPropisCtrl'
	    })
	    .state('novaGlava', {
	      url: '/novaGlava',
	      templateUrl: 'dodaj-glavu.html',
	      controller: 'addPropisCtrl'
	    })
	    .state('noviClan', {
	    	url: '/noviClan',
	    	templateUrl: 'dodaj-clan.html',
	    	controller: 'addPropisCtrl'
	    })
	    .state('noviStav', {
	    	url: '/noviStav',
	    	templateUrl: 'dodaj-stav.html',
	    	controller: 'addPropisCtrl'
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