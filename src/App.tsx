import { useEffect, useState } from 'react'
import { Menu, Play, X } from 'lucide-react'

const links = ['ABOUT', 'PROCESS', 'PROJECTS', 'CATALOG', 'D.O.T', 'TALK']

function Logo() {
  return (
    <svg aria-label="Grilled Pixels" xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 256 256" fill="none">
      <path d="M 160 88 L 194 34 L 216 0 L 256 0 L 256 40 L 221.5 93.5 L 200 128 L 256 128 L 256 256 L 96 256 L 96 168 L 64.246 220 L 40 256 L 0 256 L 0 216 L 34 162 L 56 128 L 0 128 L 0 0 L 160 0 Z" fill="white" />
    </svg>
  )
}

function App() {
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => event.key === 'Escape' && setMenuOpen(false)
    window.addEventListener('keydown', closeOnEscape)
    return () => window.removeEventListener('keydown', closeOnEscape)
  }, [])

  return (
    <main className="relative h-screen w-full overflow-hidden bg-black text-white">
      <video
        className="absolute inset-0 h-full w-full object-cover lg:scale-[1.2]"
        src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260725_114042_d2ed2a89-f2fa-449b-9609-da456344257b.mp4"
        autoPlay
        muted
        loop
        playsInline
      />

      <div className="relative z-10 flex h-full flex-col px-5 sm:px-6 md:px-10 lg:px-14">
        <header className="flex items-center justify-between py-6">
          <a href="#" aria-label="Home"><Logo /></a>
          <nav className="hidden items-center gap-8 md:flex" aria-label="Primary navigation">
            {links.map((link) => (
              <a key={link} href="#" className="text-sm tracking-wide transition-opacity hover:opacity-70">{link}</a>
            ))}
          </nav>
          <button className="p-2 transition-opacity hover:opacity-70 md:hidden" onClick={() => setMenuOpen(true)} aria-label="Open menu">
            <Menu size={24} />
          </button>
        </header>

        <section className="mt-4 grid grid-cols-2 gap-4 sm:gap-6 lg:grid-cols-4 lg:gap-8">
          <div>
            <h2 className="text-lg font-normal leading-tight tracking-wide md:text-xl">
              <span className="block">ADAM</span>
              <span className="font-pixel block text-2xl md:text-3xl">ROBERTS</span>
            </h2>
            <div className="mt-3 text-[10px] text-white/50">*</div>
            <p className="font-pixel mt-1 text-xs leading-relaxed text-white/60">
              Grilled Pixels is my<br />
              personal brand - I came up<br />
              with it in 2004 based on<br />
              &quot;cooking up ideas&quot;
            </p>
          </div>

          <div className="text-right lg:text-left">
            <h2 className="text-lg font-normal leading-tight tracking-wide md:text-xl">
              <span className="block">DESIGN &amp;</span>
              <span className="font-pixel block text-2xl md:text-3xl">ENGINEERING</span>
            </h2>
          </div>

          <div>
            <h3 className="font-pixel mb-3 text-base uppercase tracking-widest text-white/50">What I Do</h3>
            <p className="max-w-[220px] text-sm leading-relaxed text-white/90">I create the top 1% of experiences for brands and digital products</p>
          </div>

          <div className="text-right lg:text-left">
            <h3 className="font-pixel mb-3 text-base uppercase tracking-widest text-white/50">Services</h3>
            <ul className="space-y-0.5 text-sm leading-relaxed text-white/90">
              <li>- Branding</li>
              <li>- Creative Direction &amp; Strategy</li>
              <li>- UX/UI Design</li>
              <li>- Web Development (React/Nextjs)</li>
              <li>- 3D, WebGL / Photography</li>
              <li>- Video &amp; Animation</li>
            </ul>
          </div>
        </section>

        <div className="flex-1" />

        <section className="pb-4">
          <div className="grid grid-cols-1 items-end gap-4 sm:gap-6 lg:grid-cols-2">
            <h1 className="text-3xl font-normal uppercase tracking-wide sm:text-4xl md:text-5xl lg:text-[3.75rem] xl:text-[4.25rem]" style={{ lineHeight: 0.72 }}>
              I BRING THE<br />
              <span className="font-pixel inline-block text-[1.25em] leading-none align-baseline">UNEXPECTED</span> TO<br />
              BRAND &amp; DIGITAL<br />
              <span className="font-pixel inline-block text-[1.25em] leading-none align-baseline">EXPERIENCES</span>
            </h1>

            <div className="flex flex-col justify-end gap-4 sm:gap-6">
              <button className="flex items-center gap-3 self-start border border-white/30 bg-white/5 px-6 py-3 backdrop-blur-sm transition-colors hover:bg-white/10">
                <Play size={14} fill="white" />
                <span className="text-sm tracking-wider">PLAY SHOWREEL</span>
              </button>
              <div className="flex flex-wrap items-stretch gap-2 self-start text-sm text-white/80 sm:gap-3 lg:self-end">
                <div className="flex items-center gap-2 bg-[#0B0B0B] px-3 py-2 sm:px-4"><span className="text-sm font-bold tracking-tight sm:text-base">FWA</span><span className="text-xs text-white/50">x1</span></div>
                <div className="flex items-center gap-2 bg-[#0B0B0B] px-3 py-2 sm:px-4"><span className="text-lg font-bold sm:text-xl">W.</span><span className="text-xs text-white/50">x7</span></div>
                <div className="flex items-center gap-2 bg-[#0B0B0B] px-3 py-2 sm:px-4"><span className="text-[10px] font-bold tracking-tight sm:text-xs">CSSDesignAwards</span><span className="text-xs text-white/50">x22</span></div>
              </div>
            </div>
          </div>

          <footer className="mt-4 grid grid-cols-1 gap-2 pt-4 sm:mt-5 sm:grid-cols-2 sm:gap-4">
            <p className="text-xs text-white/60">Open to freelance, contract or full-time. <a className="text-red-500 transition-colors hover:text-red-400" href="#">Schedule a call</a></p>
            <p className="text-xs text-white/60 sm:text-right">5 full cases &bull; 82 archive fragments &bull; 22 catalog items</p>
          </footer>
        </section>
      </div>

      <div className={`fixed inset-0 z-50 flex flex-col bg-black/95 backdrop-blur-md transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] ${menuOpen ? 'pointer-events-auto opacity-100' : 'pointer-events-none opacity-0'}`} aria-hidden={!menuOpen}>
        <div className="flex items-center justify-between px-6 py-6">
          <Logo />
          <button className="p-2 transition-opacity hover:opacity-70" onClick={() => setMenuOpen(false)} aria-label="Close menu"><X size={24} /></button>
        </div>
        <nav className="flex flex-1 flex-col items-center justify-center gap-8" aria-label="Mobile navigation">
          {links.map((link, index) => (
            <a
              key={link}
              href="#"
              onClick={() => setMenuOpen(false)}
              className={`text-2xl tracking-widest transition-all duration-500 ease-[cubic-bezier(0.16,1,0.3,1)] ${menuOpen ? 'translate-y-0 opacity-100' : 'translate-y-4 opacity-0'}`}
              style={{ transitionDelay: menuOpen ? `${100 + index * 60}ms` : '0ms' }}
            >{link}</a>
          ))}
        </nav>
      </div>
    </main>
  )
}

export default App
