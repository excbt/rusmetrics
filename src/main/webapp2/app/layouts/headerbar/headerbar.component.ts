import { Component, OnInit, OnDestroy, AfterViewInit} from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { MainMenuService } from '../../layouts/left-main-menu/main-menu.service';
import { ProfileService } from '../profiles/profile.service';
import { JhiLanguageHelper, Principal, LoginModalService, LoginService } from '../../shared';

import { VERSION } from '../../app.constants';
import { Title } from '@angular/platform-browser';
import { Observable, Subscription } from 'rxjs';

@Component({
    selector: 'jhi-headerbar',
    templateUrl: './headerbar.component.html',
    styleUrls: ['./headerbar.component.scss']
})
export class HeaderbarComponent implements OnInit, OnDestroy, AfterViewInit {

    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    modalRef: NgbModalRef;
    version: string;

    pageTitle: string;
    pageTitleKey: string;

    private routeUrlSubscription: Subscription;

    private initTitleSubscription: Subscription;

    constructor(
        private loginService: LoginService,
        private languageService: JhiLanguageService,
        private title: Title,
        private languageHelper: JhiLanguageHelper,
        private principal: Principal,
        private loginModalService: LoginModalService,
        private profileService: ProfileService,
        private mainMenuService: MainMenuService,
        private router: Router,
    ) {
        this.version = VERSION ? 'v' + VERSION : '';

        const delay1sec = Observable.timer(1000);
        delay1sec.subscribe((x) => {
          this.pageTitle = this.title.getTitle();
        });
    }

    ngAfterViewInit() {
    }

    ngOnInit() {
        this.routeUrlSubscription = this.router.events
            .filter((event) => event instanceof NavigationEnd)
            .map(() => {
                let route = this.router.routerState.root;
                while (route.firstChild) { route = route.firstChild; }
                return route;
              })
            .filter((route) => route.outlet === 'primary')
            .flatMap((route) => route.data)
            .map((data) => {
                return data.pageTitle;
            })
            .subscribe((key) => {
                this.pageTitleKey = key;
            });

        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });

        this.profileService.getProfileInfo().then((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });

    }

    ngOnDestroy() {
        if (this.routeUrlSubscription) {
            this.routeUrlSubscription.unsubscribe();
        }

        if (this.initTitleSubscription) {
            this.initTitleSubscription.unsubscribe();
        }
    }

    toggleMainMenu() {
        this.mainMenuService.toggleMainMenu();
    }

    changeLanguage(languageKey: string) {
        this.languageService.changeLanguage(languageKey);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    logout() {
        // this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.principal.getImageUrl() : null;
    }

}
