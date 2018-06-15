import { Injectable } from '@angular/core';
import { ExcApiService } from '../exc-tools/exc-api-service';
import { HttpClient } from '@angular/common/http';

interface IconPair {
    categoryKeyname: string;
    iconKeyname: string;
}

interface BuildingType {
    keyname?: string;
    categoryKeyname: string;
}

@Injectable()
export class BuildingTypeDecoderService extends ExcApiService {

    private iconPairs: IconPair[] = [
        {categoryKeyname: 'default', iconKeyname: 'default'},
        {categoryKeyname: 'X_1000102', iconKeyname: 'childhome'},
        {categoryKeyname: 'H_COT_BUILDINGS', iconKeyname: 'cot'},
        {categoryKeyname: 'H_IND_HOUSES', iconKeyname: 'dom'},
        {categoryKeyname: 'GB_GOV_BUILDINGS', iconKeyname: 'gos'},
        {categoryKeyname: 'X_1000090', iconKeyname: 'hospital'},
        {categoryKeyname: 'NR_HEALTH', iconKeyname: 'hospital'},
        {categoryKeyname: 'X_1000070', iconKeyname: 'hotel'},
        {categoryKeyname: 'H_IND_HOUSES', iconKeyname: 'mkd'},
        {categoryKeyname: 'X_1000181', iconKeyname: 'prod'},
        {categoryKeyname: 'X_1000182', iconKeyname: 'prod'},
        {categoryKeyname: 'X_1000080', iconKeyname: 'school'},
        {categoryKeyname: 'X_1000081', iconKeyname: 'school'},
        {categoryKeyname: 'X_1000082', iconKeyname: 'school'},
        {categoryKeyname: 'X_1000083', iconKeyname: 'school'},
        {categoryKeyname: 'X_1000084', iconKeyname: 'school'},
        {categoryKeyname: 'X_1000085', iconKeyname: 'school'},
        {categoryKeyname: '', iconKeyname: 'stadium'}
    ];

    private defaultIcon = this.iconPairs[0];

    constructor(
        http: HttpClient) {
        super({apiUrl: '/building-types'}, http);
    }

    decodeBuildingTypeIcon(buildingType: BuildingType): string {
        if (!buildingType || !buildingType.categoryKeyname) {
            return this.defaultIcon.categoryKeyname;
        }
        const searchResult = this.iconPairs.filter((i) => i.categoryKeyname === buildingType.categoryKeyname);
        if (searchResult.length === 0) {
            console.log('Building type icon for ' + buildingType.categoryKeyname + ' is not found. Using default');
            return this.defaultIcon.categoryKeyname;
        }
        return searchResult[0].iconKeyname;
    }

    getIconName16(category: string): string {
        return 'exc-icon-building-' + this.decodeBuildingTypeIcon({categoryKeyname: category}) + '-16';
    }

    getIconName24(category: string): string {
        return 'exc-icon-building-' + this.decodeBuildingTypeIcon({categoryKeyname: category}) + '-24';
    }

}
