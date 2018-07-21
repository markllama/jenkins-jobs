#!/usr/bin/groovy

import com.redhat.art.RpmSpecfile

class RpmSpecfileTest extends GroovyTestCase {

    String samples

    void setUp() {
        samples = [
            '''# Filler
Version: 3.0
Release: 1
%changelog
#
Here is some stuff
$end
''',
            '''# filler
Version: 3.2
Release: 0.1.0
%changelog
stuff
and
more
stuff
but
with
no
end
marker
'''
        ]
    }


    void testConstructor() { }

    void testPropertyBody() { }


    void testPropertyVersion() { }
    void testPropertyRelease() { }

    void testPropertyChangelog() { }
}
