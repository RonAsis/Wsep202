import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ShareService} from '../../services/share.service';

@Component({
  selector: 'app-header-guest',
  templateUrl: './header-guest.component.html',
  styleUrls: ['./header-guest.component.css']
})
export class HeaderGuestComponent implements OnInit {
  @Output() featureSelected;

  constructor(private shareService: ShareService) {
    this.featureSelected = new EventEmitter<string>();
  }

  ngOnInit(): void {
    this.shareService.featureSelected.subscribe(feature => this.onSelect(feature));
  }

  onSelect(feature: string) {
    this.featureSelected.emit(feature);
  }
}
