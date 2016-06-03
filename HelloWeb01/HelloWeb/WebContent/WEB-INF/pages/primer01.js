(function(angular) {
	/**
	 * Url web server na kome se nalazi aplikacija
	 */
	var serverUrl = "//localhost:8443/HelloWeb/";

	/**
	 * Ovo je kontroler za logovanje i registraciju korisnika sistema
	 */
	var logInCtrl = function(mainService, $scope, $resource, $http, $location,
			$state) {
		$scope.token = null;
		$scope.error = null;
		$scope.gradj = false;
		$scope.odbor = false;
		$scope.predSkup = false;
		$scope.userName = '';
		$scope.passWord = '';
		$scope.rolaUser = null;

		// ovde ces nakon sto povuces podatke iz geta login-a dobiti token
		// medjutim, ako username i pass ne postoje, token se ne stvori i onda
		// pukne u klasi ApiController
		// zato sto mu saljes token koji nije validan, pa pukne u klasi.
		// TO DO -- Odradi validaciju, da ako nema korisnika, da ne pukne to
		// sranje, da ga nekako vrati samo na login i sve ispocetka.
		// Sredjen TO-DO na angularu, ali nije jos na back-end-u
		$scope.login = function() {
			$scope.error = null;
			mainService
					.login($scope.userName, $scope.passWord)
					.then(
							function(token) {
								if (token === undefined) {
									$state.go('login');
								} else {
									$scope.token = token;
									$http.defaults.headers.common.Authorization = 'Bearer '
											+ token;
									if (localStorage) {
										localStorage.setItem('key', token);
									}

									$scope.checkRoles();
								}

							}, function(error) {
								$scope.error = error
								$scope.userName = '';
							});

		}
		
		$scope.zaGradjane = function()
		{
			if($scope.usernamePassword === 'Username: g@g | Password: g')
			{
				$scope.usernamePassword = '';
			}
			else
			{
				$scope.usernamePassword = "Username: g@g | Password: g";
			}
			
		}

		/**
		 * Ovde se proveravaju role i nakon provere se rediretkuje na /main
		 * stranicu.
		 */
		$scope.checkRoles = function() {
			$http
					.get(serverUrl+'/api/role')
					.then(
							function(response) {

								$scope.rolaUser = response.data;
								if ($scope.rolaUser.role === 'odbornik'
										|| $scope.rolaUser.role === 'gradjanin'
										|| $scope.rolaUser.role === 'predsednik skupstine') {
									$state.go('main');
								} else {
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

		// ----------------------------------------------------------------GET

		/*
		 * var Users =
		 * $http.get('http://localhost:8080/HelloWeb/Index/checkUser')
		 * .success(function (data, status, headers) { $scope.user = data; })
		 * .error(function(data, status, headers) { console.log(data);
		 * console.log(status); console.log(headers); });
		 */
		// -------------------------------------------------------------------------REGISTRACIJA
		$scope.loginKor = {};
		$scope.loginKor.ime = '';
		$scope.loginKor.prezime = '';
		$scope.loginKor.username = '';
		$scope.loginKor.password = '';
		$scope.loginKor.jksPutanja = '';
		$scope.loginKor.alias = '';

		/**
		 * Metoda za registraciju korisnika na sistem
		 */
		$scope.register = function() {
			$http.post(serverUrl+'/Index/registration', {
				ime : $scope.loginKor.ime,
				prezime : $scope.loginKor.prezime,
				username : $scope.loginKor.username,
				password : $scope.loginKor.password,
				jksPutanja : $scope.loginKor.jksPutanja,
				alias : $scope.loginKor.alias
			}).success(function(user, status, headers) {
				$location.path('/main');
			});
		}

	}

	/**
	 * Kontroler za glavnu stranicu sistema, ovde lepimo na $scope klijenta koji
	 * se ulogovao.
	 */
	var mainPageCtrl = function($scope, $resource, $http, $location,
			$stateParams, $state) {
		// JAKO JAKO BITNO DA SE svaki put prilikom slanja request-a posalje i
		// token
		// mora opet da se postavlja Authorization header!!!
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');

		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda
		 * treba prebaciti odmah na login posto nema token kod sebe
		 */
		if (localStorage.getItem('key') !== null) {
			$http.get(serverUrl+'/api/role').then(
					function(response) {
						$http.get(
								serverUrl+'/Index/user/'
										+ response.data.username).then(
								function(response2) {
									$scope.rolaUser = response2.data;
								});

					});
		} else {
			$state.go('login');
		}

		$scope.pretragaAkata = function() {
			$state.go('pretragaAkata');
		};

		$scope.sviAkti = function() {
			$state.go('pregledAkata');
		};

		$scope.addAkt = function() {
			$state.go('dodPropis');
		};

		$scope.sednica = function() {
			$state.go('sednica');
		};

	}

	var sviAktiCtrl = function($scope, $resource, $http, $location,
			$stateParams, $state) {
		// JAKO JAKO BITNO DA SE svaki put prilikom slanja request-a posalje i
		// token
		// mora opet da se postavlja Authorization header!!!
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');

		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda
		 * treba prebaciti odmah na login posto nema token kod sebe
		 */
		if (localStorage.getItem('key') !== null) {
			$http.get(serverUrl+'/clan/all').then(
					function(response) {
						$scope.propisi = response.data.propisi;
					});
		} else {
			$state.go('login');
		}

		//pregled akta na osnovu njegovog ID-a
		$scope.pregledAkta = function(propisId) {
			$state.go('izabranPropis', {
				id : propisId
			});
		}
		
		//odbornici i predsednik skupstine mogu da povuku predloge propisa ili amandmana
		//pre nego sto uopste dodje do sednice
		$scope.povuciPropis = function(propisId, index)
		{
			$http.get(serverUrl+'/clan/odbijen/' + propisId)
			.success(function(data, header, status)
			{
				$scope.propisi.splice(index, 1);
				$state.go('pregledAkata');
			})
		}
		
	}

	var pregledPropisaCtrl = function($scope, $resource, $http, $location,
			$stateParams, $state) {

		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');

		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda
		 * treba prebaciti odmah na login posto nema token kod sebe
		 */
		if (localStorage.getItem('key') !== null) {
			if (!angular.equals({}, $stateParams)) {
				var propisId = $stateParams.id;
			}

			$http.get(serverUrl+'/clan/' + propisId).then(
					function(response) {
						$scope.htmlXsl = response.data;
					});
		} else {
			$state.go('login');
		}
	}

	var addPropisCtrl = function($scope, mainService, $resource, $http, $location,
			$stateParams, $state) {
		//provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		//ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
			+ localStorage.getItem('key');
		//posto imamo token, proveravamo koja je rola, ako je obican gradjanin, onda dovidjenja!
		$http.get(serverUrl+'/api/role')
		.success(function(data)
		{
			if(data.role === 'gradjanin')
			{
				$state.go('main');
			}
		})
		
		
		// predefinsane vrednosti, jer pravi problem na backend-u.
		$scope.stavBroj = '';
		$scope.stavTekst = '';
		$scope.clanTekst = '';
		$scope.glavaNaziv = '';

		/**
		 * Ovde bi faktikci trebalo da se posalje na bazu gotov propis, ali u
		 * trenutku pisanja ovog koda, mi nemamo ni | slova B ob Baze. Stoga
		 * samo kreira novi xml na hard disku.
		 */
		$scope.zavrsiPropis = function() {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.post(serverUrl+'/clan/noviPropis', {
				nazivPropisa : $scope.propis.naziv,
				nazivDeo : $scope.deo.naziv,
				nazivGlave : $scope.glavaNaziv,
				nazivClana : $scope.clan.naziv,
				opisClana : $scope.clan.opis,
				redniBrojStava : $scope.stavBroj,
				stavTekst : $scope.stavTekst,
				tekstClana : $scope.clanTekst
			}).success(function(data, header, status) {
				$state.go('opcije');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				
				$state.go('main');
			});

		}

		/**
		 * Mozemo dodati jos jedan novi deo.
		 */
		$scope.noviDeo = function() {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.post(serverUrl+'/clan/noviDeo', {
				nazivDeo : $scope.deo.naziv,
				nazivGlave : $scope.glavaNaziv,
				nazivClana : $scope.clan.naziv,
				opisClana : $scope.clan.opis,
				redniBrojStava : $scope.stavBroj,
				stavTekst : $scope.stavTekst,
				tekstClana : $scope.clanTekst
			}).success(function(data, header, status) {
				$state.go('opcije');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				
				$state.go('main');
			});

		}

		/**
		 * Mozemo dodati jos jednu novu glavu.
		 */
		$scope.novaGlava = function() {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.post(serverUrl+'/clan/novaGlava', {
				nazivGlave : $scope.glavaNaziv,
				nazivClana : $scope.clan.naziv,
				opisClana : $scope.clan.opis,
				redniBrojStava : $scope.stavBroj,
				stavTekst : $scope.stavTekst,
				tekstClana : $scope.clanTekst
			}).success(function(data, header, status) {
				$state.go('opcije');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				
				$state.go('main');
			});
		}

		$scope.noviClan = function() {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.post(serverUrl+'/clan/noviClan', {
				nazivClana : $scope.clan.naziv,
				opisClana : $scope.clan.opis,
				redniBrojStava : $scope.stavBroj,
				stavTekst : $scope.stavTekst,
				tekstClana : $scope.clanTekst
			}).success(function(data, header, status) {
				$state.go('opcije');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				
				$state.go('main');
			});
		}

		$scope.noviStav = function() {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.post(serverUrl+'/clan/noviStav', {
				redniBrojStava : $scope.stavBroj,
				stavTekst : $scope.stavTekst
			}).success(function(data, header, status) {
				$state.go('opcije');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				
				$state.go('main');
			});
		}

		$scope.saveToDatabase = function() {
			$http.get(serverUrl+'/clan/save').then(
					function(response) {
						$state.go('main');
					});
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.get(serverUrl+'/clan/save')
			.success(function(data, header, status) {
				$state.go('main');
			}).error(function(data, header, status) {
				if(header === 406)
				{
					alert("Your certificate is invalid.");
				}
				else
				{
					alert("Something went wrong, log in again, please.");
				}
				$state.go('main');
			});

		}

	}

	var sednicaCtrl = function($rootScope, $scope, $resource, $http, $location,
			$stateParams, $state, propisService) 
	{
		
		$scope.izborAkata = function()
		{
			$state.go('sednicaIzborAkata');
		}
		
		//ovo je lista koja sluzi da cuva podatke o tome koji akti su izabrani za sednicu
		$scope.listaIzabranihAkataPoImenu = [];
		
		
		$scope.toggleSelection = function toggleSelection(propisId) 
		{
			var idx = $scope.listaIzabranihAkataPoImenu.indexOf(propisId);

			// is currently selected
			if (idx > -1) {
				$scope.listaIzabranihAkataPoImenu.splice(idx, 1);
			}
			    // is newly selected
			else {
				$scope.listaIzabranihAkataPoImenu.push(propisId);
			}
		};
		
		//saljemo preko servica podatke o tome koji su izabrani propisi
		propisService.setProperty($scope.listaIzabranihAkataPoImenu);
		
		$scope.zapocniSednicu = function()
		{
			$state.go('procesSednice');
		}

	}
	
	
	
	var procesSedniceCtrl = function($scope, $http, $state, propisService)
	{
		$scope.izabraniAkti = [];
		
		var lista = propisService.getProperty();
		
		for(var i=0; i<lista.length; i++)
		{
			$http.get(serverUrl +'/clan/naziv/' + lista[i])
			.success(function(data, header, status)
			{
				$scope.izabraniAkti.push(data);
				
			})
			.error(function(data, header, status)
			{
				console.log(data);
				console.log(header);
				console.log(status);
			});
		}
		
		$scope.pregledAkta = function(propisId)
		{
			$state.go('pregledAktaZaNacelo', {id: propisId});
		}
	}
	
	
	
	
	var izabranPropisNaceloCtrl = function($scope, $http, $state, $stateParams)
	{
		if(!angular.equals({}, $stateParams))
		{
			var propisId = $stateParams.id;
		}
		$http.get(serverUrl+'/clan/' + propisId)
		.success(function(data, header, status)
		{
			$scope.htmlXsl = data;
			
		})
		.error(function(data, header, status)
		{
			console.log(data);
			console.log(header);
			console.log(status);
		});
		
		$scope.odbijenPropis = function()
		{
			if(!angular.equals({}, $stateParams))
			{
				var propisId = $stateParams.id;
			}
			$http.get(serverUrl+'/clan/odbijen/' + propisId)
			.success(function(data, header, status)
			{
				$state.go('procesSednice');
			})
			.error(function(data, header, status)
			{
				console.log(data);
				console.log(header);
				console.log(status);
			});
		}
	}
	

	/**
	 * Angular kontroler zadužen za pronalaženje akata po različitim kriterijuma i sadržajima.
	 * Za sada je implementirana mogućnost pretreaživanja po sadržaju.
	 */
	var pretragaAkataCtrl = function($scope, $state, $resource, $http) {
		var PretragaAkataResurs = $resource(serverUrl+'/clan/pretragaPropisa',
									  null,{
										pretrazi:{method:'POST'}
									  });
				
		$scope.pretraga = {};
		$scope.pretraga.upit = "";
		$scope.pretraga.rezultat = [];

		$scope.pretraga.pretrazi = function() {
			PretragaAkataResurs.pretrazi({
				upit: $scope.pretraga.upit
			},function(data) {
				$scope.pretraga.rezultat = data.propisi;
			});
		};
				
		$scope.pretraga.pregledAkta = function(propisId) {
			$state.go('izabranPropis', {
				id : propisId
			});
		};
	};

	var app = angular
			.module('app', [ 'ui.router', 'ngResource', 'ngSanitize' ]);
	app.controller('logInCtrl', logInCtrl);
	app.controller('mainPageCtrl', mainPageCtrl);
	app.controller('sviAktiCtrl', sviAktiCtrl);
	app.controller('pretragaAkataCtrl', pretragaAkataCtrl);
	app.controller('addPropisCtrl', addPropisCtrl);
	app.controller('pregledPropisaCtrl', pregledPropisaCtrl);
	app.controller('sednicaCtrl', sednicaCtrl);
	app.controller('procesSedniceCtrl', procesSedniceCtrl);
	app.controller('izabranPropisNaceloCtrl', izabranPropisNaceloCtrl);

	app.config(function($stateProvider, $urlRouterProvider) {

		$urlRouterProvider.otherwise('/login');

		$stateProvider.state('login', {
			url : '/login',
			templateUrl : 'logIn.html',
			controller : 'logInCtrl'
		}).state('main', {
			url : '/main',
			templateUrl : 'main.html',
			controller : 'mainPageCtrl'
		}).state('register', {
			url : '/register',
			templateUrl : 'reg-unos.html',
			controller : 'logInCtrl'
		}).state('pregledAkata', {
			url : '/sviAkti',
			templateUrl : 'sviAkti.html',
			controller : 'sviAktiCtrl'
		}).state('pretragaAkata', {
			url : '/pretragaAkata',
			templateUrl : 'pretraga-akata.html',
			controller : 'pretragaAkataCtrl'
		}).state('sednica', {
			url : '/sednica',
			templateUrl : 'sednica-izbor.html',
			controller : 'sednicaCtrl'
		}).state('sednicaIzborAkata', {
			url : '/sednica/izborAkata',
			templateUrl : 'sednica-izbor-akata.html',
			controller : 'sednicaCtrl'
		}).state('procesSednice', {
			url : '/sednica/procesSednice',
			templateUrl : 'proces-sednice-nacelo.html',
			controller : 'procesSedniceCtrl'
		}).state('pregledAktaZaNacelo', {
			url : '/sednica/procesSednice/akt/:id',
			templateUrl : 'pregled-akta-za-nacelo.html',
			controller : 'izabranPropisNaceloCtrl'
		}).state('izabranPropis', {
			url : '/propisi/propis/:id',
			templateUrl : 'pregled-akta.html',
			controller : 'pregledPropisaCtrl'
		}).state('dodPropis', {
			url : '/noviPropis',
			templateUrl : 'dodaj-propis.html',
			controller : 'addPropisCtrl'
		}).state('opcije', {
			url : '/opcije',
			templateUrl : 'opcije.html',
			controller : 'addPropisCtrl'
		}).state('noviDeo', {
			url : '/noviDeo',
			templateUrl : 'dodaj-deo.html',
			controller : 'addPropisCtrl'
		}).state('novaGlava', {
			url : '/novaGlava',
			templateUrl : 'dodaj-glavu.html',
			controller : 'addPropisCtrl'
		}).state('noviClan', {
			url : '/noviClan',
			templateUrl : 'dodaj-clan.html',
			controller : 'addPropisCtrl'
		}).state('noviStav', {
			url : '/noviStav',
			templateUrl : 'dodaj-stav.html',
			controller : 'addPropisCtrl'
		}).state('greska', {
			url : '/greska',
			templateUrl : 'greska.html'
		});

	});

	app.service('mainService', function($http, $state) {
		return {
			login : function(email, lozinka) {
				return $http.post(
						serverUrl+'/Index/checkUser', {
							username : email,
							password : lozinka
						}).then(function(response) {

					return response.data.token;

				});
			},

			hasRole : function() {
				return $http.get(serverUrl+'/api/role')
						.then(function(response) {
							console.log(response);
							return response.data;
						});
			},
			logOut: function()
			{
					$http.defaults.headers.common.Authorization = '';
					localStorage.clear();
					$state.go('login');
			},	
		};
	});
	app.service('propisService', function()
	{
		var list = [];

        return {
            getProperty: function () {
                return list;
            },
            setProperty: function(value) {
                list = value;
            }
        };
	});

}(angular));