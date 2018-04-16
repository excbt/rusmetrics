import { MatPaginatorIntl } from '@angular/material';

export function ruPaginatorIntl() {
    const p = new MatPaginatorIntl();
    p.itemsPerPageLabel = 'Количество на странице';
    p.nextPageLabel = 'След страница';
    p.previousPageLabel = 'Пред страница';
    p.firstPageLabel = 'Первая страница';
    p.lastPageLabel = 'Последняя страница';
    return p;
}
