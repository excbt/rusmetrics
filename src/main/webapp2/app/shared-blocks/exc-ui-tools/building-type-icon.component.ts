import { Component, OnInit, Input } from '@angular/core';
import { BuildingTypeDecoderService } from './building-type-decoder.service';

@Component({
  selector: 'jhi-exc-building-type-icon',
  template: `<i class="{{ getBuidingTypeIcon() }}"></i>`
})
export class BuildingTypeIconComponent implements OnInit {

  @Input() categoryName: string;
  @Input() size: string;

  constructor(private typeDecoderService: BuildingTypeDecoderService) { }

  ngOnInit() {
  }

  getBuidingTypeIcon(): string {
    if (this.size === '24px') {
      return this.typeDecoderService.getIconName24(this.categoryName);
    } else {
      return this.typeDecoderService.getIconName16(this.categoryName);
    }
  }

}
