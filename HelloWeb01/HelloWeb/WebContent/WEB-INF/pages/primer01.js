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

		$scope.zaGradjane = function() {
			if ($scope.usernamePassword === 'Username: g@g | Password: g') {
				$scope.usernamePassword = '';
			} else {
				$scope.usernamePassword = "Username: g@g | Password: g";
			}

		}

		/**
		 * Ovde se proveravaju role i nakon provere se rediretkuje na /main
		 * stranicu.
		 */
		$scope.checkRoles = function() {
			$http
					.get(serverUrl + '/api/role')
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
		
//		$scope.register = function() {
//			$http.post(serverUrl + '/Index/registration', {
//				ime : $scope.loginKor.ime,
//				prezime : $scope.loginKor.prezime,
//				username : $scope.loginKor.username,
//				password : $scope.loginKor.password,
//				jksPutanja : $scope.loginKor.jksPutanja,
//				alias : $scope.loginKor.alias
//			}).success(function(user, status, headers) {
//				$location.path('/main');
//			});
//		}

	}
	var registerCtrl = function($scope, $resource, $http, $location,
			$stateParams, $state) {
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin') {
				$state.go('main');
			}
		})
		
		
		$scope.register = function() {
			$http.post(serverUrl + '/Index/registration', {
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
			$http.get(serverUrl + '/api/role').then(
					function(response) {
						$http.get(
								serverUrl + '/Index/user/'
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
		$scope.addAmandman = function() {
			$state.go('dodAmandman');
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
			$http.get(serverUrl + '/clan/all').then(function(response) {
				$scope.propisi = response.data.propisi;
			});
		} else {
			$state.go('login');
		}

		// pregled akta na osnovu njegovog ID-a
		$scope.pregledAkta = function(propisId) {
			$state.go('izabranPropis', {
				id : propisId
			});
		}

		// odbornici i predsednik skupstine mogu da povuku predloge propisa ili
		// amandmana
		// pre nego sto uopste dodje do sednice
		$scope.povuciPropis = function(propisId, index) {
			$http.get(serverUrl + '/clan/odbijen/' + propisId).success(
					function(data, header, status) {
						$scope.propisi.splice(index, 1);
						$state.go('pregledAkata');
					})
		}

		$scope.toPdf = function(propisId) {
			$http.get(serverUrl + "clan/toPdf/" + propisId).success(
					function(data, header, status) {
						var text = "Your " + data.naziv
								+ "pdf is succesfully created."
						alert(text);
					}).error(function(data, header, status) {
				alert("Something went wrong. Please try again.")
			})
		}

	}

	var addAmandmanCtrl = function($scope, $resource, $http, $location,
			$stateParams, $state) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin') {
				$state.go('main');
			}
		})

		/*
		 * proverimo da li se u localStorage nalazi token, ako se ne nalazi onda
		 * treba prebaciti odmah na login posto nema token kod sebe
		 */
		if (localStorage.getItem('key') !== null) {
			$http.get(serverUrl + '/clan/all').then(function(response) {
				$scope.propisi = [];
				var propisiLista = response.data.propisi;
				for(var i=0; i<propisiLista.length; i++)
				{
					if(propisiLista[i].status !== 'U_CELINI')
					{
						$scope.propisi.push(propisiLista[i]);
					}
					
				}
			});
		} else {
			$state.go('login');
		}

		$scope.pronadji = function(selektovaniPropis) {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}

			$scope.propisProba = JSON.parse(selektovaniPropis);

			$scope.listaClanova = [];

			for (var i = 0; i < $scope.propisProba.deo.length; i++) {
				var deo = $scope.propisProba.deo[i];
				if (deo.glava !== null && deo.glava !== undefined
						&& deo.glava.length != 0) {
					var glave = deo.glava;
					for (var j = 0; j < glave.length; j++) {
						for (var k = 0; k < glave[j].clan.length; k++) {
							$scope.listaClanova.push(glave[j].clan[k]);
						}
					}
				} else {

					for (var j = 0; j < deo.clan.length; j++) {
						$scope.listaClanova.push(deo.clan[j]);
					}
				}
			}

		}

		$scope.pronadjiClan = function(selektovaniClan) {

			$scope.clan = JSON.parse($scope.selektovaniClan);
			$scope.check = true;
		}

		$scope.pronadjiStav = function(selektovaniStav) {

			$scope.stav = JSON.parse(selektovaniStav);

		}

		$scope.zavrsiAmandman = function() {
			if ($scope.clan.sadrzaj.tekst.length === 0) {
				$http.post(serverUrl + 'amandman/novi', {
					propisId : $scope.propisProba.id,
					clanId : $scope.clan.id,
					clanNaziv : $scope.clan.naziv,
					stavTekst : $scope.stav.tekst,
					stavRedniBroj : $scope.stav.redniBroj,
					amandmanObrazlozenje : $scope.amandman.obrazlozenje
				}).success(function(data, status, header) {
					$state.go('main');
				});
			}
			if ($scope.stav === undefined) {
				$http.post(serverUrl + 'amandman/novi', {
					propisId : $scope.propisProba.id,
					clanId : $scope.clan.id,
					clanNaziv : $scope.clan.naziv,
					clanTekst : $scope.clan.sadrzaj.tekst[0].text,
					amandmanObrazlozenje : $scope.amandman.obrazlozenje
				}).success(function(data, status, header) {
					$state.go('main');
				});
			}

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

			$http.get(serverUrl + 'clan/' + propisId).success(
					function(data, status, header) {
						$scope.htmlXsl = data;
					}).error(function(data, status, header) {
				if (status === 406) {
					alert('Your document does not have a valid signature.');
					$state.go('main');
				} else {
					alert('Your document is not signed.');
					$state.go('main');
				}
			})
		} else {
			$state.go('login');
		}
	}

	var addPropisCtrl = function($scope, mainService, $resource, $http,
			$location, $stateParams, $state) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin') {
				$state.go('main');
			}
		})

		// predefinsane vrednosti, jer pravi problem na backend-u.
		$scope.stavBroj = '';
		$scope.stavTekst = '';
		$scope.clanTekst = '';
		$scope.glavaNaziv = '';
		$scope.clanNaziv = '';
		$scope.clanoviListaPost = '';
		$scope.propisNazivPost = '';

		$scope.zavrsiPropisXml = function() {

			$http.post(serverUrl + 'clan/propisXml', {
				propisXml : $scope.propisXml
			}).success(function(data) {
				alert("Radi");
			});

		}

		$http.get(serverUrl + '/clan/all').then(function(response) {
			$scope.propisi = response.data.propisi;
		});

		$scope.pronadji = function(selektovaniPropis) {
			$scope.propisProba = JSON.parse(selektovaniPropis);
			$scope.propisNazivPost = $scope.propisProba.naziv;
			if ($scope.clan === undefined) {
				$scope.stavTekst = 'Na osnovu propisa '
						+ $scope.propisProba.naziv.replace(/\s+/g, '') + ' ';
			} else if ($scope.clan.izbor === 'Stav') {
				$scope.stavTekst = 'Na osnovu propisa '
						+ $scope.propisProba.naziv.replace(/\s+/g, '') + ' ';
			} else {
				$scope.clanTekst = 'Na osnovu propisa '
						+ $scope.propisProba.naziv.replace(/\s+/g, '') + ' ';
			}
			$scope.listaClanova = [];

			for (var i = 0; i < $scope.propisProba.deo.length; i++) {
				var deo = $scope.propisProba.deo[i];
				if (deo.glava !== null && deo.glava !== undefined
						&& deo.glava.length != 0) {
					var glave = deo.glava;
					for (var j = 0; j < glave.length; j++) {
						for (var k = 0; k < glave[j].clan.length; k++) {
							$scope.listaClanova.push(glave[j].clan[k]);
						}
					}
				} else {

					for (var j = 0; j < deo.clan.length; j++) {
						$scope.listaClanova.push(deo.clan[j]);
					}
				}
			}
		}

		$scope.ubaciClan = function(selektovanClan) {
			$scope.clanParse = JSON.parse(selektovanClan);

			$scope.clanNaziv = $scope.clanParse.naziv;

			var temp = $scope.clanTekst.split('clana');
			var temp1 = $scope.stavTekst.split('clana');
			if ($scope.clan === undefined) {
				$scope.stavTekst = temp1[0] + 'clana '
						+ $scope.clanParse.naziv.replace(/\s+/g, '');
			} else if ($scope.clan.izbor === 'Stav') {
				$scope.stavTekst = temp1[0] + 'clana '
						+ $scope.clanParse.naziv.replace(/\s+/g, '');
			} else {
				$scope.clanTekst = temp[0] + 'clana '
						+ $scope.clanParse.naziv.replace(/\s+/g, '');
			}

			$scope.clanoviListaPost = $scope.clanParse.id;

		}

		$scope.zavrsiPropis = function(isValid) {
			if (isValid) {
				if (localStorage.getItem('key') === null) {
					mainService.logOut();
				}
				if ($scope.clan.izbor === 'Stav') {
					$scope.splitovanTekst = $scope.stavTekst.split(' ');
				} else {
					$scope.splitovanTekst = $scope.clanTekst.split(' ');
				}
				if ($scope.referenca === 'da') {
					$http
							.post(serverUrl + 'clan/noviPropis', {
								nazivPropisa : $scope.propis.naziv,
								nazivDeo : $scope.deo.naziv,
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst,
								referenciraniClanovi : $scope.clanoviListaPost,
								refernciranPropis : $scope.propisNazivPost,
								nazivClanaRef : $scope.clanNaziv,
								splitovanTekstClan : $scope.splitovanTekst,
								propisId : $scope.propisProba.id
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				} else {
					$http
							.post(serverUrl + 'clan/noviPropis', {
								nazivPropisa : $scope.propis.naziv,
								nazivDeo : $scope.deo.naziv,
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				}
			} else {
				alert('Your data inputs are not inserted properly!');
			}
		}

		/**
		 * Mozemo dodati jos jedan novi deo.
		 */
		$scope.noviDeo = function(isValid) {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			if (isValid) {
				if ($scope.clan.izbor === 'Stav') {
					$scope.splitovanTekst = $scope.stavTekst.split(' ');
				} else {
					$scope.splitovanTekst = $scope.clanTekst.split(' ');
				}

				if ($scope.referenca === 'da') {
					$http
							.post(serverUrl + 'clan/noviDeo', {
								nazivDeo : $scope.deo.naziv,
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst,
								referenciraniClanovi : $scope.clanoviListaPost,
								refernciranPropis : $scope.propisNazivPost,
								nazivClanaRef : $scope.clanNaziv,
								splitovanTekstClan : $scope.splitovanTekst,
								propisId : $scope.propisProba.id
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				} else {
					$http
							.post(serverUrl + '/clan/noviDeo', {
								nazivDeo : $scope.deo.naziv,
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				}
			} else {
				alert('Your data inputs are not inserted properly!');
			}
		}

		/**
		 * Mozemo dodati jos jednu novu glavu.
		 */
		$scope.novaGlava = function(isValid) {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			if (isValid) {
				if ($scope.clan.izbor === 'Stav') {
					$scope.splitovanTekst = $scope.stavTekst.split(' ');
				} else {
					$scope.splitovanTekst = $scope.clanTekst.split(' ');
				}

				if ($scope.referenca === 'da') {
					$http
							.post(serverUrl + 'clan/novaGlava', {
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst,
								referenciraniClanovi : $scope.clanoviListaPost,
								refernciranPropis : $scope.propisNazivPost,
								nazivClanaRef : $scope.clanNaziv,
								splitovanTekstClan : $scope.splitovanTekst,
								propisId : $scope.propisProba.id
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				} else {
					$http
							.post(serverUrl + '/clan/novaGlava', {
								nazivGlave : $scope.glavaNaziv,
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				}
			} else {
				alert('Your data inputs are not inserted properly!');
			}
		}

		$scope.noviClan = function(isValid) {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			if (isValid) {
				if ($scope.clan.izbor === 'Stav') {
					$scope.splitovanTekst = $scope.stavTekst.split(' ');
				} else {
					$scope.splitovanTekst = $scope.clanTekst.split(' ');
				}

				if ($scope.referenca === 'da') {
					$http
							.post(serverUrl + 'clan/noviClan', {
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst,
								referenciraniClanovi : $scope.clanoviListaPost,
								refernciranPropis : $scope.propisNazivPost,
								nazivClanaRef : $scope.clanNaziv,
								splitovanTekstClan : $scope.splitovanTekst,
								propisId : $scope.propisProba.id
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				} else {
					$http
							.post(serverUrl + '/clan/noviClan', {
								nazivClana : $scope.clan.naziv,
								opisClana : $scope.clan.opis,
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								tekstClana : $scope.clanTekst
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				}
			} else {
				alert('Your data inputs are not inserted properly!');
			}
		}

		$scope.noviStav = function(isValid) {
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			if (isValid) {
				$scope.splitovanTekst = $scope.stavTekst.split(' ');

				if ($scope.referenca === 'da') {
					$http
							.post(serverUrl + 'clan/noviStav', {
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst,
								referenciraniClanovi : $scope.clanoviListaPost,
								refernciranPropis : $scope.propisNazivPost,
								nazivClanaRef : $scope.clanNaziv,
								splitovanTekstClan : $scope.splitovanTekst,
								propisId : $scope.propisProba.id
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				} else {
					$http
							.post(serverUrl + '/clan/noviStav', {
								redniBrojStava : $scope.stavBroj,
								stavTekst : $scope.stavTekst
							})
							.success(function(data, header, status) {
								$state.go('opcije');
							})
							.error(
									function(data, header, status) {
										if (header === 406) {
											alert("Your certificate is invalid.");
										} else {
											alert("Something went wrong, please log in again.");
										}

										$state.go('main');
									});
				}
			} else {
				alert('Your data inputs are not inserted properly!');
			}
		}

		$scope.saveToDatabase = function() {
			$http.get(serverUrl + '/clan/save').then(function(response) {
				$state.go('main');
			});
			if (localStorage.getItem('key') === null) {
				mainService.logOut();
			}
			$http.get(serverUrl + '/clan/save').success(
					function(data, header, status) {
						$state.go('main');
					}).error(function(data, header, status) {
				if (header === 406) {
					alert("Your certificate is invalid.");
				} else {
					alert("Something went wrong, please log in again.");
				}
				$state.go('main');
			});

		}

	}

	var sednicaCtrl = function($rootScope, $scope, $resource, $http, $location,
			$stateParams, $state, propisService) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'odbornik') {
				$state.go('main');
			}
		})
		
		$scope.izborAkata = function() {
			$state.go('sednicaIzborAkata');
		}

		// ovo je lista koja sluzi da cuva podatke o tome koji akti su izabrani
		// za sednicu
		$scope.listaIzabranihAkataPoImenu = [];

		$scope.toggleSelection = function toggleSelection(propisId) {
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

		// saljemo preko servica podatke o tome koji su izabrani propisi
		propisService.setProperty($scope.listaIzabranihAkataPoImenu);

		$scope.zapocniSednicu = function() {
			$state.go('procesSednice');
		}

	}

	var procesSedniceCtrl = function($scope, $http, $state, propisService) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'odbornik') {
				$state.go('main');
			}
		})
		$scope.izabraniAkti = [];

		var lista = propisService.getProperty();

		for (var i = 0; i < lista.length; i++) {
			$http.get(serverUrl + '/clan/naziv/' + lista[i]).success(
					function(data, header, status) {
						$scope.izabraniAkti.push(data);

					}).error(function(data, header, status) {
				console.log(data);
				console.log(header);
				console.log(status);
			});
		}

		$scope.pregledAkta = function(propisId) {
			$http.get(serverUrl + 'clan/id/' + propisId)
			.success(function(data, header, status){
				if(data.status === 'U_NACELU'){
					$state.go('prihvatanjeAmandmana',{
						id : propisId
					});
				}else{
					$state.go('pregledAktaZaNacelo', {
						id : propisId
					});
				}
			});
		}
	}

	var izabranPropisNaceloCtrl = function($scope, $http, $state, $stateParams,
			propisService) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'obornik') {
				$state.go('main');
			}
		})

		if (!angular.equals({}, $stateParams)) {
			var propisId = $stateParams.id;
		}
		$http.get(serverUrl + '/clan/' + propisId).success(
				function(data, header, status) {
					$scope.htmlXsl = data;

				}).error(function(data, header, status) {
			console.log(data);
			console.log(header);
			console.log(status);
		});

		$scope.odbijenPropis = function() {

			var propisServiceLista = propisService.getProperty();
			for (var i = 0; i < propisServiceLista.length; i++) {
				if (propisServiceLista[i] == $stateParams.id) {
					// delete propisServiceLista[i];
					propisServiceLista.splice(i, 1);

					break;
				}
			}
			propisService.setProperty(propisServiceLista);

			if (!angular.equals({}, $stateParams)) {
				var propisId = $stateParams.id;
			}
			$http.get(serverUrl + 'clan/odbijen/' + $stateParams.id).success(
					function(data, header, status) {
						if (propisService.getProperty().length == 0) {
							$state.go('sednicaIzborAkata');
						} else {
							$state.go('procesSednice');
						}
					}).error(function(data, header, status) {
				console.log('ejjjj');
				console.log(data);
				console.log(header);
				console.log(status);
			});
		}

		$scope.potvrdaUNacelu = function() {
			if (!angular.equals({}, $stateParams)) {
				var propisId = $stateParams.id;
			}
			$http.post(serverUrl + '/clan/prihvacenUNacelu/' + propisId)
					.success(function(data, header, status) {
						$state.go('prihvatanjeAmandmana', {
							id : propisId
						});
					});
		};
	}

	/**
	 * Angular kontroler zadužen za pronalaženje akata po različitim kriterijuma
	 * i sadržajima. Za sada je implementirana mogućnost pretreaživanja po
	 * sadržaju.
	 */
	var pretragaAkataCtrl = function($scope, $state, $resource, $http) {
		var PretragaAkataResurs = $resource(
				serverUrl + '/clan/pretragaPropisa', null, {
					pretrazi : {
						method : 'POST'
					}
				});

		$scope.pretraga = {};
		$scope.pretraga.upit = "";
		$scope.pretraga.rezultat = [];

		$scope.pretraga.pretrazi = function() {
			PretragaAkataResurs.pretrazi({
				upit : $scope.pretraga.upit
			}, function(data) {
				$scope.pretraga.rezultat = data.propisi;
			});
		};

		$scope.pretraga.pregledAkta = function(propisId) {
			$state.go('izabranPropis', {
				id : propisId
			});
		};
	};

	var prihvatanjeAmandmanaCtrl = function($scope, $state, $resource, $http,
			$stateParams, amandmanService) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin') {
				$state.go('main');
			}
		})
		if (!angular.equals({}, $stateParams)) {
			var propisId = $stateParams.id;

			$http.get(serverUrl + 'clan/id/' + propisId).success(
					function(data, header, status) {
						$scope.propis = data;
					});

			// ////
			// ovo je lista koja sluzi da cuva podatke o tome koji amandmani su
			// izabrani za sednicu
			$scope.listaIzabranihAmandmana = [];

			$scope.toggleSelection = function toggleSelection(amandmanId) {
				var idx = $scope.listaIzabranihAmandmana.indexOf(amandmanId);

				// is currently selected
				if (idx > -1) {
					$scope.listaIzabranihAmandmana.splice(idx, 1);
				}
				// is newly selected
				else {
					$scope.listaIzabranihAmandmana.push(amandmanId);
				}
			};

			// saljemo preko servica podatke o tome koji su izabrani propisi
			amandmanService.setProperty($scope.listaIzabranihAmandmana);

			$scope.nastaviSaAmandmanima = function() {
				$state.go('pregledAmandmanaZaPrihvatanje', {
					id : propisId
				});
			}
		}
	};

	var pregledAmandmanaZaPrihvatanjeCtrl = function($scope, $http,
			amandmanService, $stateParams, $state) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'odbornik') {
				$state.go('main');
			}
		})
		
		if (!angular.equals({}, $stateParams)) {
			var propisId = $stateParams.id;
		}

		$scope.amandmani = amandmanService.getProperty();

		$scope.pregledAmandmana = function(amandmanId) {
			$state.go('razmatranjeAmandmana', {
				id : propisId,
				id2 : amandmanId
			});
		}
	};

	var razmatranjeAmandmanaCtrl = function($scope, $http, $stateParams,
			$state, amandmanService) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'odbornik') {
				$state.go('main');
			}
		})
		if (!angular.equals({}, $stateParams)) {
			var propisId = $stateParams.id;
			var amandmanId = $stateParams.id2;
		}

		$http.defaults.headers.common.Authorization = 'Bearer '
			+ localStorage.getItem('key');
		
		$http.get(serverUrl + 'amandman/' + amandmanId).success(
				function(data, header, status) {
					$scope.htmlXsl = data;
				});

		$scope.potvrdaAmandmana = function() {
			$http.post(serverUrl + 'amandman/potvrda', {
				propisId : propisId,
				amandmanId : amandmanId
			}).success(function(data, header, status) {
				// alert('Promenjeno, pogledaj na bazi.');
				amandmanService.getProperty().shift();
				if (amandmanService.getProperty().length != 0) {
					$state.go('pregledAmandmanaZaPrihvatanje', {
						id : propisId
					});
				} else {
					// $state.go('sednicaIzborAkata');
					$state.go('potvrdaPropisaUCelosti', {
						id : propisId
					});
				}

			})

		}
		
		$scope.odbijenAmandman = function(){
			$http.get(serverUrl + 'amandman/propis/'+propisId+'/delete/'+amandmanId)
			.success(function(data, header, status){
				$state.go('procesSednice');
			});
		}

		$scope.toPdf = function() {
			$http.get(serverUrl + 'amandman/toPdf/' + amandmanId).success(
					function(data, status, header) {
						alert('Your amandman pdf is created');
					}).error(function(data, status, header) {
				alert('Something went wrong, please try again.');
			})
		}
	}

	var potvrdaPropisaUCelostiCtrl = function($scope, $state, $http,
			$stateParams) {
		// provera da li ima token u localStorage-u
		if (localStorage.getItem('key') === null) {
			$state.go('login');
		}
		// ako ima, zalepi ga na http
		$http.defaults.headers.common.Authorization = 'Bearer '
				+ localStorage.getItem('key');
		// posto imamo token, proveravamo koja je rola, ako je obican gradjanin,
		// onda dovidjenja!
		$http.get(serverUrl + '/api/role').success(function(data) {
			if (data.role === 'gradjanin' || data.role === 'odbornik') {
				$state.go('main');
			}
		})
		
		if (!angular.equals({}, $stateParams)) {
			var propisId = $stateParams.id;
		}
		$http.get(serverUrl + '/clan/' + propisId).success(
				function(data, header, status) {
					$scope.htmlXsl = data;

				}).error(function(data, header, status) {
			console.log(data);
			console.log(header);
			console.log(status);
		});

		$scope.odbijenPropis = function() {
			$state.go('procesSednice');
		}

		$scope.potvrdaUCelini = function() {
			if (!angular.equals({}, $stateParams)) {
				var propisId = $stateParams.id;
			}

			$http.post(serverUrl + 'clan/prihvacenUCelini/' + propisId)
					.success(function(data, header, status) {
						$state.go('sednicaIzborAkata');
					});
			
			$http.get(serverUrl + 'clan/id/' + propisId)
			.success(function(data, header, status)
			{
				$http.get('../../IstorijskiArhiv/iasgns/propis/' + data.naziv)
				.success(function(data, status, header)
				{
					alert('File is successfully sent to IASGNS.');
				})
				.error(function(data, status, header)
				{
					alert("Something went wrong.");
				})
			})
		};
	};

	var prikazClanaCtrl = function($scope, $state, $http, $stateParams) {
		if (!angular.equals({}, $stateParams)) {
			var clanId = $stateParams.idClana;
			var nazivPropisa = $stateParams.nazivPropisa;
		}

		$http
				.get(
						serverUrl + 'clan/propis/' + nazivPropisa + '/clan/'
								+ clanId).success(function(data) {
					$scope.xmlToXsl = data;
				});
	}

	var app = angular
			.module('app', [ 'ui.router', 'ngResource', 'ngSanitize' ]);
	app.controller('logInCtrl', logInCtrl);
	app.controller('registerCtrl', registerCtrl);
	app.controller('mainPageCtrl', mainPageCtrl);
	app.controller('sviAktiCtrl', sviAktiCtrl);
	app.controller('pretragaAkataCtrl', pretragaAkataCtrl);
	app.controller('addPropisCtrl', addPropisCtrl);
	app.controller('addAmandmanCtrl', addAmandmanCtrl);
	app.controller('pregledPropisaCtrl', pregledPropisaCtrl);
	app.controller('sednicaCtrl', sednicaCtrl);
	app.controller('procesSedniceCtrl', procesSedniceCtrl);
	app.controller('izabranPropisNaceloCtrl', izabranPropisNaceloCtrl);
	app.controller('prihvatanjeAmandmanaCtrl', prihvatanjeAmandmanaCtrl);
	app.controller('pregledAmandmanaZaPrihvatanjeCtrl',
			pregledAmandmanaZaPrihvatanjeCtrl);
	app.controller('razmatranjeAmandmanaCtrl', razmatranjeAmandmanaCtrl);
	app.controller('potvrdaPropisaUCelostiCtrl', potvrdaPropisaUCelostiCtrl);
	app.controller('prikazClanaCtrl', prikazClanaCtrl);

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
			controller : 'registerCtrl'
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
		}).state('dodAmandman', {
			url : '/noviAmandman',
			templateUrl : 'dodaj-amandman.html',
			controller : 'addAmandmanCtrl'
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
		}).state('prihvatanjeAmandmana', {
			url : '/sednica/procesSednice/akt/:id/amandmani',
			templateUrl : 'proces-sednice-amandmani.html',
			controller : 'prihvatanjeAmandmanaCtrl'
		}).state('pregledAmandmanaZaPrihvatanje', {
			url : '/sednica/procesSednice/akt/:id/amandmaniPrikaz',
			templateUrl : 'pregledAmandmanaZaPrihvatanje.html',
			controller : 'pregledAmandmanaZaPrihvatanjeCtrl'
		}).state('razmatranjeAmandmana', {
			url : '/sednica/procesSednice/akt/:id/amandman/:id2',
			templateUrl : 'amandman-razmatranje.html',
			controller : 'razmatranjeAmandmanaCtrl'
		}).state('potvrdaPropisaUCelosti', {
			url : '/sednica/procesSednice/akt/:id/ucelini',
			templateUrl : 'proces-sednica-ucelini.html',
			controller : 'potvrdaPropisaUCelostiCtrl'
		}).state('prikazClana', {
			url : '/propis/:nazivPropisa/clan/:idClana',
			templateUrl : 'prikaz-clan.html',
			controller : 'prikazClanaCtrl'
		});

	});

	app.service('mainService', function($http, $state) {
		return {
			login : function(email, lozinka) {
				return $http.post(serverUrl + '/Index/checkUser', {
					username : email,
					password : lozinka
				}).then(function(response) {

					return response.data.token;

				});
			},

			hasRole : function() {
				return $http.get(serverUrl + '/api/role').then(
						function(response) {
							console.log(response);
							return response.data;
						});
			},
			logOut : function() {
				$http.defaults.headers.common.Authorization = '';
				localStorage.clear();
				$state.go('login');
			},
		};
	});
	app.service('propisService', function() {
		var list = [];

		return {
			getProperty : function() {
				return list;
			},
			setProperty : function(value) {
				list = value;
			}
		};
	});
	app.service('amandmanService', function() {
		var list = [];

		return {
			getProperty : function() {
				return list;
			},
			setProperty : function(value) {
				list = value;
			}
		};
	});

}(angular));