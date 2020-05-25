import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-owner-item',
  templateUrl: './owner-item.component.html',
  styleUrls: ['./owner-item.component.css']
})
export class OwnerItemComponent implements OnInit {

  @Input() username: string;
  @Output() ownerItemDeleted = new EventEmitter<string>();
  constructor() { }

  ngOnInit(): void {
  }

  onOwnerItemDeleted($event: MouseEvent) {
    this.ownerItemDeleted.emit(this.username);
  }
}
