import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
// import { MatToolbarModule } from '@angular/material/toolbar';
// import { MatButtonModule } from '@angular/material/button';
// import { MatIconModule } from '@angular/material/icon';
import { MainMenuService } from '../../layouts/left-main-menu/main-menu.service';

@Component({
  selector: 'jhi-headerbar',
  templateUrl: './headerbar.component.html',
  styleUrls: [
    'headerbar.scss'
  ]
})
export class HeaderbarComponent implements OnInit {

  constructor(
      private mainMenuService: MainMenuService,
      private router: Router
  ) { }

  ngOnInit() {
  }

  toggleMainMenu() {
    this.mainMenuService.toggleMainMenu();
  }

  logout() {
    // this.collapseNavbar();
    // this.loginService.logout();
    this.router.navigate(['']);
}

}
