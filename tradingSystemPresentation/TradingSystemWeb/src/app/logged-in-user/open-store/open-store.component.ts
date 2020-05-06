import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'app-open-store',
  templateUrl: './open-store.component.html',
  styleUrls: ['./open-store.component.css']
})
export class OpenStoreComponent implements OnInit {
  messageReg: string;
  @ViewChild('storeNameInput', {static: false}) storeNameInputRef: ElementRef;

  constructor() { }

  ngOnInit(): void {
  }

  onOpenStore() {
    console.log(`open store`);
    this.messageReg = `The store is Open`;
  }

  onClearDetails() {
    this.messageReg = '';
    this.storeNameInputRef.nativeElement.value = '';
  }
}
