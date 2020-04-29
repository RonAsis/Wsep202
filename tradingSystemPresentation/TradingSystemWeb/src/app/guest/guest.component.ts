import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-guest',
  templateUrl: './guest.component.html',
  styleUrls: ['./guest.component.css']
})
export class GuestComponent implements OnInit {
  loadedFeature = 'Login';

  constructor() { }

  ngOnInit(): void {
  }

  onNavigate(feature: string) {
    this.loadedFeature = feature;
  }
}
