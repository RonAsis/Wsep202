import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-header-guest',
  templateUrl: './header-guest.component.html',
  styleUrls: ['./header-guest.component.css']
})
export class HeaderGuestComponent implements OnInit {
  @Output() featureSelected = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

  onSelect(feature: string) {
    this.featureSelected.emit(feature);
  }
}
