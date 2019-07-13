/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { CatalogappTestModule } from '../../../test.module';
import { TransmissionComponent } from 'app/entities/transmission/transmission.component';
import { TransmissionService } from 'app/entities/transmission/transmission.service';
import { Transmission } from 'app/shared/model/transmission.model';

describe('Component Tests', () => {
  describe('Transmission Management Component', () => {
    let comp: TransmissionComponent;
    let fixture: ComponentFixture<TransmissionComponent>;
    let service: TransmissionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CatalogappTestModule],
        declarations: [TransmissionComponent],
        providers: []
      })
        .overrideTemplate(TransmissionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TransmissionComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TransmissionService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Transmission(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.transmissions[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
